package com.cardreaderapp.activities;

import android.os.Bundle;

import com.cardreaderapp.models.Upload;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cardreaderapp.R;
import com.cardreaderapp.adapters.CardsListAdapter;
import com.cardreaderapp.models.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

public class CardsListFragment extends Fragment {
    CardsListAdapter mAdapter;
    Vector<Upload> mData = new Vector<Upload>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;

    DatabaseReference  mDatabaseRef;
     FirebaseUser mUserDetails;
    private FloatingActionButton mAddCardBtn;

    public CardsListFragment() {
        // Required empty public constructor


    }
    public  void ShowData()
    {
        mUserDetails=FirebaseAuth.getInstance().getCurrentUser();
        String userUid = mUserDetails.getUid();
        FirebaseDatabase.getInstance().getReference("Users/" + userUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mData.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Upload c=new Upload ();
                    c.mAddress=(ds.child("mAddress").getValue().toString());
                    c.mCompany=(ds.child("mCompany").getValue().toString());
                    c.mEmail=(ds.child("mEmail").getValue().toString());
                    c.mName=(ds.child("mName").getValue().toString());
                    c.mPhone=(ds.child("mPhone").getValue().toString());
                    c.mWebsite=(ds.child("mWebsite").getValue().toString());
                    c.mImageUri=ds.child("mImageUri").getValue().toString();
                    mData.add(c);


                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

        // TODO: Navigate to cardDetails fragment
        mAdapter.setOnItemClickListener(new CardsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                Navigation.findNavController(view).navigate(R.id.action_cardsListFragment_to_cardDetailsFragment);
            }
        });

        mAddCardBtn = view.findViewById(R.id.cards_list_add_bt);
        mAddCardBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_cardsListFragment_to_newCardFragment));
        ShowData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
