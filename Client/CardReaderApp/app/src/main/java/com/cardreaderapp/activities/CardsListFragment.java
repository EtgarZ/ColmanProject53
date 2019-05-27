package com.cardreaderapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cardreaderapp.models.Card;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.adapters.CardsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import static android.app.Activity.RESULT_OK;

public class CardsListFragment extends Fragment {
    CardsListAdapter mAdapter;
    Vector<Card> mData = new Vector<Card>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    DatabaseReference  mDatabaseRef;
     FirebaseUser mUserDetails;
    private FloatingActionButton mAddCardBtn;
    private FloatingActionButton mShareCardsBtn;

    private ProgressBar mProgressBar;

    public CardsListFragment() {
        // Required empty public constructor


    }

    private void prepareUIForLoading(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mAddCardBtn.hide();
        mShareCardsBtn.hide();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void prepareUIAfterLoading(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mAddCardBtn.show();
        mShareCardsBtn.show();
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void ShowData()
    {
        String userUid = mUserDetails.getUid();
        FirebaseDatabase.getInstance().getReference("Users/" + userUid + "/Cards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Card c = new Card();
                    c.setPersonName(ds.child("personName").getValue().toString());
                    c.setPhoneNumber(ds.child("phoneNumber").getValue().toString());
                    c.setCompany(ds.child("company").getValue().toString());
                    c.setAddress(ds.child("address").getValue().toString());
                    c.setEmail(ds.child("email").getValue().toString());
                    c.setWebsite(ds.child("website").getValue().toString());
                    c.setImageUri(ds.child("imageUri").getValue().toString());
                    mData.add(c);
                }
                mAdapter.notifyDataSetChanged();
                prepareUIAfterLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Fetching card data failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_cards_list, container, false);
        mRecyclerView = view.findViewById(R.id.cards_list_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new CardsListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        mUserDetails=FirebaseAuth.getInstance().getCurrentUser();

        // TODO: Navigate to cardDetails fragment
        mAdapter.setOnItemClickListener(new CardsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                Card card = mData.elementAt(index);
                CardsListFragmentDirections.ActionCardsListFragmentToCardDetailsFragment action =
                        CardsListFragmentDirections.actionCardsListFragmentToCardDetailsFragment(
                                card.getPersonName(),
                                card.getPhoneNumber(),
                                card.getCompany(),
                                card.getAddress(),
                                card.getEmail(),
                                card.getWebsite(),
                                Uri.parse(card.getImageUri())
                        );
                Navigation.findNavController(view).navigate(action);
            }
        });

        mAddCardBtn = view.findViewById(R.id.cards_list_add_bt);
        //mAddCardBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_cardsListFragment_to_newCardFragment));
        mAddCardBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onSelectImageClick();
                    }
                }
        );

        mShareCardsBtn = view.findViewById(R.id.cards_list_share_bt);
        mShareCardsBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_cardsListFragment_to_usersListFragment));

        mProgressBar = view.findViewById(R.id.cards_list_pb);
        mProgressBar.setVisibility(View.INVISIBLE);
        return view;
    }

    public void onSelectImageClick() {
        // From docs :
        // If image is blurred/low quality, You are probably using the thumbnail image.
        // You need to set the EXTRA_OUTPUT to a path and camera will save the full image to this path.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri outputFileUri = Uri.fromFile(new File(this.getContext().getExternalCacheDir().getPath(), "pickImageResult.jpeg"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setRequestedSize(1024, 1024, CropImageView.RequestSizeOptions.RESIZE_INSIDE)
                //.setRequestedSize(400, 400)
                //.start(this.getActivity());
                .start(this.getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        try {
            // handle result of CropImageActivity
            if (requestCode ==  CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
            {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    CardsListFragmentDirections.ActionCardsListFragmentToNewCardFragment action =
                            CardsListFragmentDirections.actionCardsListFragmentToNewCardFragment(result.getUri());
                    Navigation.findNavController(getView()).navigate(action);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Toast.makeText(this.getActivity(), "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        BackgroundTask task = new BackgroundTask();
        task.execute();
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {

        public BackgroundTask() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ShowData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            prepareUIForLoading();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }
}
