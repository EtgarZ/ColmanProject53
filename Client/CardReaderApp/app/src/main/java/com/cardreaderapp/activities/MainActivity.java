package com.cardreaderapp.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
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
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnCamera = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imageView);


        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final Bitmap bitmap = (Bitmap )data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);

        Button btnExtractInfo = findViewById(R.id.btnExtractInfo);
        btnExtractInfo.setVisibility(View.VISIBLE);

        btnExtractInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String encodedBitmap = Base64Converter.ConvertToBase64(bitmap);
                Card card = null;
                try {
                    card = RestService.GetRestService().GetCardData("bla.jpg", encodedBitmap);

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
        });
    }
}