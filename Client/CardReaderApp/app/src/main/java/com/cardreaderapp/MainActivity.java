package com.cardreaderapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

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
        Bitmap bitmap = (Bitmap )data.getExtras().get("data");
        imageView.setImageBitmap(bitmap);

        Button btnExtractInfo = findViewById(R.id.btnExtractInfo);
        btnExtractInfo.setVisibility(View.VISIBLE);

        btnExtractInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Connect to server and send bitmap. wait for response
//                Socket socket = serverSocket.accept();
//                InputStream inputStream = socket.getInputStream();
//                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String content = br.readLine();
//                System.out.println(content);

                String contactData = "{'Name':'Moti Levi'" +
                        ",'Phone':'08-678943212'" +
                        "'Email':'Moti.Levi22@gmail.com'";

                String name = "Moti Levi";
                String phone = "08-678943212";
                String email = "Moti.Levi22@gmail.com";

                // Open contact member and fill details
                // Create intent contact-add
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                // Get card data to contact member
                intent.putExtra(ContactsContract.Intents.Insert.NAME, name)
                        .putExtra(ContactsContract.Intents.Insert.PHONE, phone)
                        .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                        .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                                ContactsContract.CommonDataKinds.Email.TYPE_WORK);

                startActivity(intent);
            }
        });
    }
}
