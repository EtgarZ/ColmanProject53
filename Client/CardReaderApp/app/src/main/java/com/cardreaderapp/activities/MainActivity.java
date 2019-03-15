package com.cardreaderapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.cardreaderapp.R;
import com.cardreaderapp.api.RestService;
import com.cardreaderapp.models.Card;
import com.cardreaderapp.utils.Base64Converter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public static final int PICK_IMAGE = 1;
    boolean isPicked = false;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnPickImage = findViewById(R.id.btnPickImage);

        Button btnCamera = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imageView);

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPicked = true;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPicked = false;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (data == null)
                return;

            Button btnExtractInfo = findViewById(R.id.btnExtractInfo);
            btnExtractInfo.setVisibility(View.VISIBLE);

            if (isPicked)
            {
                Uri imageUri = data.getData();
                imageView.setImageURI(imageUri);
                final Bitmap galleryBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                btnExtractInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExtractDataAndOpenContact(galleryBitmap);
                    }
                });
            }
            else
            {
                final Bitmap cameraBitmap = (Bitmap )data.getExtras().get("data");
                imageView.setImageBitmap(cameraBitmap);
                btnExtractInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ExtractDataAndOpenContact(cameraBitmap);
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
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
