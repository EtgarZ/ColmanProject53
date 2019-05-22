package com.cardreaderapp.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.cardreaderapp.R;
import com.google.firebase.FirebaseApp;

public class MainActivity extends AppCompatActivity {

    private NavController mNavController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavController = Navigation.findNavController(this,R.id.main_navigation);
        NavigationUI.setupActionBarWithNavController(this, mNavController);
        FirebaseApp.initializeApp(this);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                mNavController.navigateUp();
                break;
            case R.id.card_details_edit_item:
                return false; // will handle in fragment callback
            case R.id.card_details_delete_item:
                return false; // will handle in fragment callback
        }
        return super.onOptionsItemSelected(item);
    }


}
