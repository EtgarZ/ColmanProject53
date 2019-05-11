package com.cardreaderapp.activities;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.adapters.UsersListAdapter;
import com.cardreaderapp.models.Card;
import com.cardreaderapp.models.Share;
import com.cardreaderapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsersListFragment extends Fragment {
    UsersListAdapter mAdapter;
    Map<String, User> mHashMap = new HashMap<>();
    Vector<User> mData = new Vector<>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    DatabaseReference mDatabaseRef;
    FirebaseUser mCurrentUser;
    private String mTargetUserId;

    private ProgressBar mProgressBar;

    public UsersListFragment() {
        // Required empty public constructor
    }

    private void prepareUIForLoading(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void prepareUIAfterLoading(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void ShowData()
    {
        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mHashMap.clear();
                mData.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String name = ds.child("name").getValue().toString();
                    String email = ds.child("email").getValue().toString();
                    String token = ds.child("token").getValue().toString();
                    Boolean isPro = Boolean.valueOf(ds.child("pro").getValue().toString());
                    Vector<Card> cards = new Vector<>();
                    if (ds.hasChild("Cards")){
                        for (DataSnapshot cs: ds.child("Cards").getChildren()) {
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
                    }

//                    Vector<Share> shares = new Vector<Share>();
//                    if (ds.hasChild("Shares")){
//                        for (DataSnapshot cs: ds.child("Shares").getChildren()) {
//                            Share s = new Share();
//                            c.setPersonName(cs.child("personName").getValue().toString());
//                            c.setPhoneNumber(cs.child("phoneNumber").getValue().toString());
//                            c.setCompany(cs.child("company").getValue().toString());
//                            c.setAddress(cs.child("address").getValue().toString());
//                            c.setEmail(cs.child("email").getValue().toString());
//                            c.setWebsite(cs.child("website").getValue().toString());
//                            c.setImageUri(cs.child("imageUri").getValue().toString());
//                            cards.add(c);
//                        }
//                    }

                    User user = new User(name, email, isPro, token, cards);
                    mHashMap.put(ds.getKey(), user);
                    mData.add(user);
                }
                mAdapter.notifyDataSetChanged();
                prepareUIAfterLoading();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Fetching user data failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_users_list, container, false);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mRecyclerView = view.findViewById(R.id.users_list_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new UsersListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);


        mAdapter.setOnItemClickListener(new UsersListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","user item click: " + index);
                //Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
                final User user = mData.elementAt(index);

                for (Map.Entry<String, User> entry: mHashMap.entrySet())
                {
                    if (entry.getValue().equals(user))
                        mTargetUserId = entry.getKey();
                }

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                // TODO: create new share obj in user/Shares and send notification to user token
                                Vector<Card> cards = mHashMap.get(mCurrentUser.getUid()).getCards();
                                String sender = mCurrentUser.getUid();
                                Share share = new Share(sender, cards);

                                //UpdateTargetUserId(user);
                                String shareId = FirebaseDatabase.getInstance().getReference("Users/" + mTargetUserId + "/Shares").push().getKey();
                                FirebaseDatabase.getInstance().getReference("Users/" + mTargetUserId + "/Shares").child(shareId).setValue(share);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("You are about to share your cards with " + user.getName() + ".\nConfirm to proceed")
                        .setPositiveButton("Confirm", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).show();


//                Navigation.findNavController(view).navigate(action);
            }
        });

        mProgressBar = view.findViewById(R.id.users_list_pb);
        mProgressBar.setVisibility(View.INVISIBLE);

        return view;
    }


    private void UpdateTargetUserId(final User user){

        Query queryRef = FirebaseDatabase.getInstance().getReference("Users").orderByChild("name").equalTo(user.getName());

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //TODO auto generated
                mTargetUserId = dataSnapshot.getKey();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                //TODO auto generated;
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //TODO auto generated;
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO auto generated
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO auto generated
            }
        });

//        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot val : dataSnapshot.getChildren()){
//
//                    if (val.child("name").getValue().equals(user.getName())
//                            && val.child("email").getValue().equals(user.getEmail())
//                            && val.child("token").getValue().equals(user.getToken())){
//                        mTargetUserId = val.getKey();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    if (ds.child("name").getValue().toString().equals(user.getName())
//                    && ds.child("email").getValue().toString().equals(user.getEmail())
//                    && ds.child("token").getValue().toString().equals(user.getToken())){
//                        mTargetUserId = ds.getKey();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Toast.makeText(getActivity(),"Fetching card data failed: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
        UsersListFragment.BackgroundTask task = new UsersListFragment.BackgroundTask();
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
