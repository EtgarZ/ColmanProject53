package com.cardreaderapp.activities;


import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.models.Card;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCardDetailsFragment extends Fragment {

    private ImageView mImageView;
    private EditText mName;
    private EditText mPhone;
    private EditText mCompany;
    private EditText mAddress;
    private EditText mEmail;
    private EditText mWebsite;
    private Button mSaveBtn;

    private boolean mIsNewCard;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String mUserId;
    private Uri mImageUri;
    private ProgressDialog mDialog;
    public EditCardDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_card_details, container, false);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads/" + mUserId);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/" + mUserId + "/Cards");

        String name =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getName();
        String phone =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getPhone();
        String company =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getCompany();
        String address =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getAddress();
        String email =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getEmail();
        String website =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getWebsite();
        mImageUri = EditCardDetailsFragmentArgs.fromBundle(getArguments()).getImageUri();
        mIsNewCard = EditCardDetailsFragmentArgs.fromBundle(getArguments()).getIsNewCard();

        mImageView = view.findViewById(R.id.edit_card_details_imageView);
        mName = view.findViewById(R.id.edit_card_details_name_et);
        mPhone = view.findViewById(R.id.edit_card_details_phone_et);
        mCompany = view.findViewById(R.id.edit_card_details_company_et);
        mAddress = view.findViewById(R.id.edit_card_details_address_et);
        mEmail = view.findViewById(R.id.edit_card_details_email_et);
        mWebsite = view.findViewById(R.id.edit_card_details_website_et);
        mSaveBtn = view.findViewById(R.id.edit_card_details_save_bt);

        mImageView.setImageURI(mImageUri);
        mName.setText(name);
        mPhone.setText(phone);
        mCompany.setText(company);
        mAddress.setText(address);
        mEmail.setText(email);
        mWebsite.setText(website);
        //Picasso.with(this.getContext()).load(mImageUri).fit().centerCrop().into(mImageView);
        Picasso.with(this.getContext()).load(mImageUri).fit().into(mImageView);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackgroundTask().execute();
            }
        });

        return view;
    }

    private String getFileExtension(Uri uri){
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    private void SaveCard(Uri downloadUri){
        final String name =  mName.getText().toString();
        final String phone =  mPhone.getText().toString();
        final String company =  mCompany.getText().toString();
        final String address =  mAddress.getText().toString();
        final String email =  mEmail.getText().toString();
        final String website =  mWebsite.getText().toString();

        if (mIsNewCard){
            Card card = new Card(name, phone, company, address, email, website, downloadUri.toString());
            String cardId = mDatabaseRef.push().getKey();
            mDatabaseRef.child(cardId).setValue(card);
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
            Navigation.findNavController(getView()).navigate(R.id.action_editCardDetailsFragment_to_cardsListFragment);
        }
        else{
            // Edit record
            final Card card = new Card(name, phone, company, address, email, website, mImageUri.toString());

            mDatabaseRef.addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Get map of cards in datasnapshot
                            Map<String,Object> cards = (Map<String,Object>) dataSnapshot.getValue();
                            for (Map.Entry<String, Object> entry : cards.entrySet()){

                                //Get card map
                                Map cardEntry = (Map) entry.getValue();
                                if (cardEntry.get("mImageUri").toString() == mImageUri.toString()){
                                    String cardId = entry.getKey();
                                    mDatabaseRef.child(cardId).setValue(card);
                                    Toast.makeText(getActivity(), "Card updated successfully!", Toast.LENGTH_SHORT).show();

                                    if (mDialog.isShowing()) {
                                        mDialog.dismiss();
                                    }
                                    Navigation.findNavController(getView()).navigate(R.id.action_editCardDetailsFragment_to_cardsListFragment);
                                    return;
                                }
                            }
                            Toast.makeText(getActivity(), "Couldn't update card..", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }
                    });
        }
    }

    private void UploadFile(){

        final StorageReference fileRef = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
        UploadTask uploadTask = fileRef.putFile(mImageUri);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return fileRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Toast.makeText(EditCardDetailsFragment.this.getActivity(), "Upload successfully!", Toast.LENGTH_SHORT).show();
                    SaveCard(downloadUri);
                } else {
                    Toast.makeText(EditCardDetailsFragment.this.getActivity(), "Something got wrong.. pls try again", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private class BackgroundTask extends AsyncTask<Void, Void, Void> {
        public BackgroundTask() {
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (mIsNewCard)
                UploadFile();
            else
                SaveCard(mImageUri);
            return null;
        }

        @Override
        protected void onPreExecute() {
            mDialog = ProgressDialog.show(getActivity(), "", "Saving Data...", true);
        }
    }
}
