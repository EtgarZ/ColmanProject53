package com.cardreaderapp.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.cardreaderapp.activities.UsersListFragment;
import com.cardreaderapp.adapters.UsersListAdapter;
import com.cardreaderapp.models.Card;
import com.cardreaderapp.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Vector;

public class NotificationAgreeListener extends BroadcastReceiver {

    private Context mContext;
    private String mFromUserId;
    private String mToUserId;
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(intent.getExtras().getInt("notification_id"));
        mContext = context;
        mFromUserId = intent.getExtras().getString("from_user_id");
        mToUserId = intent.getExtras().getString("to_user_id");

        FirebaseDatabase.getInstance().getReference("Users/" + mFromUserId + "/Cards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Vector<Card> cards = new Vector<>();
                for (DataSnapshot cs : dataSnapshot.getChildren()) {

                    Card c = new Card();
                    c.setPersonName(cs.child("personName").getValue().toString());
                    c.setPhoneNumber(cs.child("phoneNumber").getValue().toString());
                    c.setCompany(cs.child("company").getValue().toString());
                    c.setAddress(cs.child("address").getValue().toString());
                    c.setEmail(cs.child("email").getValue().toString());
                    c.setWebsite(cs.child("website").getValue().toString());
                    c.setImageUri(cs.child("imageUri").getValue().toString());
                    cards.add(c);
                }
                addCards(cards);
                FirebaseDatabase.getInstance().getReference("Users/" + mFromUserId + "/Cards").removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext,"Fetching user data failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addCards(Vector<Card> cards)
    {
        for(Card card: cards){
            String cardId = FirebaseDatabase.getInstance().getReference("Users/" + mToUserId + "/Cards").push().getKey();
            FirebaseDatabase.getInstance().getReference("Users/" + mToUserId + "/Cards").child(cardId).setValue(card);
        }
        Toast.makeText(mContext, "Cards added successfully!", Toast.LENGTH_SHORT).show();
    }
}
