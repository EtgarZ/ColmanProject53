package com.cardreaderapp.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.api.RestService;
import com.cardreaderapp.models.Card;
import com.cardreaderapp.utils.Base64Converter;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewCardFragment extends Fragment {

    private ImageView mIimageView;
    private Button mExtractInfoBtn;

    private Uri mImageUri;

    private final int PICK_IMAGE = 10;

    public NewCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_card, container, false);

        mIimageView = v.findViewById(R.id.imageView);
        mExtractInfoBtn = v.findViewById(R.id.btnExtractInfo);

        //onSelectImageClick();

        mImageUri = NewCardFragmentArgs.fromBundle(getArguments()).getImageUri();
        mIimageView.setImageURI(mImageUri);
        try {
            final Bitmap galleryBitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), mImageUri);
            mExtractInfoBtn.setVisibility(View.VISIBLE);
            mExtractInfoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BackgroundTask myTask = new BackgroundTask();
                    myTask.execute(galleryBitmap);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return v;
    }

    private Card extractCardData(Bitmap bitmap){
        Card card = null;
        try {
            String encodedBitmap = Base64Converter.ConvertToBase64(bitmap);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String currentDateTimeString = dateFormat.format(new Date());
            card = RestService.GetRestService().GetCardData( currentDateTimeString+".jpg", encodedBitmap);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return card;
    }

    private void navigateEditCardDetails(Card card, Uri imageUri)
    {
        NewCardFragmentDirections.ActionNewCardFragmentToEditCardDetailsFragment action =
                NewCardFragmentDirections.actionNewCardFragmentToEditCardDetailsFragment(
                                card.getPersonName(),
                                card.getPhoneNumber(),
                                card.getCompany(),
                                card.getAddress(),
                                card.getEmail(),
                                card.getWebsite(),
                                imageUri,
                        true
                        );
        Navigation.findNavController(this.getView())
                .navigate(action);
    }

    private class BackgroundTask extends AsyncTask <Bitmap,String, Card> {
        private ProgressDialog dialog;

        public BackgroundTask() {
        }

        @Override
        protected Card doInBackground(Bitmap... bitmap) {
            return extractCardData(bitmap[0]);
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "Extracting Data...", "Please wait for results...", true);
        }

        @Override
        protected void onPostExecute(Card card) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (card == null){
                Toast.makeText(getActivity(), "Couldn't retrieve data!", Toast.LENGTH_LONG).show();
                return;
            }
            navigateEditCardDetails(card, mImageUri);
        }
    }
}
