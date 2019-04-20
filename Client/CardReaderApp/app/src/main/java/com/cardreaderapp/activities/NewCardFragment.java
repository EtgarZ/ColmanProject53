package com.cardreaderapp.activities;


import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.ContactsContract;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewCardFragment extends Fragment {

    private ImageView mIimageView;
    private Button mExtractInfoBtn;

    public NewCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_card, container, false);


        Button btnAddCard = v.findViewById(R.id.btnAddCard);
        mIimageView = v.findViewById(R.id.imageView);
        mExtractInfoBtn = v.findViewById(R.id.btnExtractInfo);

        btnAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick();
            }
        });

        return v;
    }

    public void onSelectImageClick() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setRequestedSize(400, 400)
                //.start(this.getActivity());
                .start(this.getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
        {
            mExtractInfoBtn.setVisibility(View.INVISIBLE);
            mIimageView.setImageResource(0); // clear image of image view control
            String errorMessage = "You didn't select card!";
            Toast.makeText(this.getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // handle result of CropImageActivity
            if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = result.getUri();
                    mIimageView.setImageURI(imageUri);
                    final Bitmap galleryBitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                    mExtractInfoBtn.setVisibility(View.VISIBLE);
                    mExtractInfoBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Card card = extractCardData(galleryBitmap);
                            if (card == null){
                                Toast.makeText(getActivity(), "Couldn't retrieve data!", Toast.LENGTH_LONG).show();
                                return;
                            }
                            navigateEditCardDetails(card, imageUri);
                            //openContact(card);
                        }
                    });
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this.getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                                card.GetPersonName(),
                                card.GetPhoneNumber(),
                                card.GetCompany(),
                                card.GetAddress(),
                                card.GetEmail(),
                                card.GetWebsite(),
                                imageUri
                        );
        Navigation.findNavController(this.getView())
                .navigate(action);
    }

    private void openContact(Card card)
    {
        // Create intent contact-add
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        // Send card data to contact intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, card.GetPersonName())
                .putExtra(ContactsContract.Intents.Insert.PHONE, card.GetPhoneNumber())
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, card.GetEmail())
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.COMPANY, card.GetCompany())
                .putExtra(ContactsContract.Intents.Insert.POSTAL, card.GetAddress())
                .putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

        ArrayList<ContentValues> data = new ArrayList<ContentValues>();

        ContentValues row1 = new ContentValues();
        row1.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
        row1.put(ContactsContract.CommonDataKinds.Website.DATA1, card.GetWebsite());
        data.add(row1);

        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        // Open contact member and fill details from card
        startActivity(intent);
    }

}
