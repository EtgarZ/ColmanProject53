package com.cardreaderapp.activities;

import android.os.Bundle;
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

import java.util.Vector;

public class CardsListFragment extends Fragment {
    CardsListAdapter mAdapter;
    Vector<Card> mData = new Vector<Card>();

    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;


    private FloatingActionButton mAddCardBtn;

    public CardsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cards_list, container, false);
        mRecyclerView = view.findViewById(R.id.cards_list_rv);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this.getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        for (int i=1;i<=6;i++){
            mData.add(new Card("card " + i, String.valueOf(i), "company " + i,
                    "address " + i, "email " + i, "website " + i));
        }

        mAdapter = new CardsListAdapter(mData);
        mRecyclerView.setAdapter(mAdapter);

        // TODO: Navigate to cardDetails fragment
        mAdapter.setOnItemClickListener(new CardsListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int index) {
                Log.d("TAG","item click: " + index);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                Student st = StudentsListAdapter.mData.elementAt(index);
//                StudentDetailsFragment sdf =
//                        StudentDetailsFragment.newInstance(Integer.toString(st.mId), st.mName, st.mPhone, st.mAddress, st.mIsPresent, index);
//                fragmentTransaction.replace(R.id.main_container, sdf);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mAddCardBtn = view.findViewById(R.id.cards_list_add_bt);
        mAddCardBtn.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_cardsListFragment_to_newCardFragment));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }
}
