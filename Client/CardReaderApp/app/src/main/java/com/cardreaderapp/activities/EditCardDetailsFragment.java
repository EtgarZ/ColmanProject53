package com.cardreaderapp.activities;


import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
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
import com.cardreaderapp.models.Upload;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditCardDetailsFragment extends Fragment {

    private ImageView mImage;
    private EditText mName;
    private EditText mPhone;
    private EditText mCompany;
    private EditText mAddress;
    private EditText mEmail;
    private EditText mWebsite;
    private Button mSaveBtn;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private String mUserId;
    private Uri mImageUri;
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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/" + mUserId);

        String name =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getName();
        String phone =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getPhone();
        String company =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getCompany();
        String address =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getAddress();
        String email =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getEmail();
        String website =  EditCardDetailsFragmentArgs.fromBundle(getArguments()).getWebsite();
        mImageUri = EditCardDetailsFragmentArgs.fromBundle(getArguments()).getImageUri();

        mImage = view.findViewById(R.id.edit_card_details_imageView);
        mName = view.findViewById(R.id.edit_card_details_name_et);
        mPhone = view.findViewById(R.id.edit_card_details_phone_et);
        mCompany = view.findViewById(R.id.edit_card_details_company_et);
        mAddress = view.findViewById(R.id.edit_card_details_address_et);
        mEmail = view.findViewById(R.id.edit_card_details_email_et);
        mWebsite = view.findViewById(R.id.edit_card_details_website_et);
        mSaveBtn = view.findViewById(R.id.edit_card_details_save_bt);

        mImage.setImageURI(mImageUri);
        mName.setText(name);
        mPhone.setText(phone);
        mCompany.setText(company);
        mAddress.setText(address);
        mEmail.setText(email);
        mWebsite.setText(website);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadFile();
            }
        });

        return view;
    }

    private String getFileExtension(Uri uri){
        return MimeTypeMap.getFileExtensionFromUrl(uri.toString());
    }

    private void UploadFile(){
        final String name =  mName.getText().toString();
        final String phone =  mPhone.getText().toString();
        final String company =  mCompany.getText().toString();
        final String address =  mAddress.getText().toString();
        final String email =  mEmail.getText().toString();
        final String website =  mWebsite.getText().toString();

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
                    Upload upload = new Upload(name, phone, company, address, email, website, downloadUri.toString());
                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(uploadId).setValue(upload);
                } else {
                    Toast.makeText(EditCardDetailsFragment.this.getActivity(), "Something got wrong.. pls try again", Toast.LENGTH_LONG).show();
                }
            }
        });


        /*.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //set progress bar to 0
                    }
                }, 500);
                Toast.makeText(EditCardDetailsFragment.this.getActivity(), "Upload successfully!", Toast.LENGTH_SHORT).show();
                Upload upload = new Upload(name, phone, company, address, email, website, taskSnapshot.getUploadSessionUri().toString());
                String uploadId = mDatabaseRef.push().getKey();
                mDatabaseRef.child(uploadId).setValue(upload);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditCardDetailsFragment.this.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = 100.0 * taskSnapshot.getBytesTransferred()/ taskSnapshot.getTotalByteCount();
                // set progressbar with progress
            }
        });*/
    }

}
