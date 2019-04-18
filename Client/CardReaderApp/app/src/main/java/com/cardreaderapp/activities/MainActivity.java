package com.cardreaderapp.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.api.RestService;
import com.cardreaderapp.models.Card;
import com.cardreaderapp.utils.Base64Converter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CAPTURE = 0;
    public static final int PICK_IMAGE = 1;
    private static final int PIC_CROP = 2;

    Uri picUri;
    boolean isPicked = false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPickImage = findViewById(R.id.btnPickImage);

        imageView = findViewById(R.id.imageView);

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Button btnExtractInfo = findViewById(R.id.btnExtractInfo);
        if (resultCode != RESULT_OK)
        {
            btnExtractInfo.setVisibility(View.INVISIBLE);
            imageView.setImageResource(0); // clear image of image view control
            String errorMessage = "You didn't select card!";
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            switch (requestCode)
            {
                // handle result of CropImageActivity
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri imageUri = result.getUri();
                        ((ImageView) findViewById(R.id.imageView)).setImageURI(imageUri);
                        final Bitmap galleryBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                        btnExtractInfo.setVisibility(View.VISIBLE);
                        btnExtractInfo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ExtractDataAndOpenContact(galleryBitmap);
                            }
                        });
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSelectImageClick() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setRequestedSize(400, 400)
                .start(this);
    }

    private void startCropImageActivity() {
        CropImage.activity()
                .start(this);
    }

    private void performCrop(){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 2);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 512);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private void ExtractDataAndOpenContact(Bitmap bitmap)
    {
        try {
            String encodedBitmap = Base64Converter.ConvertToBase64(bitmap);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String currentDateTimeString = dateFormat.format(new Date());
            Card card = RestService.GetRestService().GetCardData( currentDateTimeString+".jpg", encodedBitmap);

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
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
