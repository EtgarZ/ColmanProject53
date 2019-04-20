package com.cardreaderapp.activities;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cardreaderapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardDetailsFragment extends Fragment {

    private TextView mName;
    private TextView mPhone;
    private TextView mCompany;
    private TextView mAddress;
    private TextView mEmail;
    private TextView mWebsite;

    private Button mEditBtn;

    public CardDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_details, container, false);

        mName = view.findViewById(R.id.card_details_name_tv);
        mPhone = view.findViewById(R.id.card_details_phone_tv);
        mCompany = view.findViewById(R.id.card_details_company_tv);
        mAddress = view.findViewById(R.id.card_details_address_tv);
        mEmail = view.findViewById(R.id.card_details_email_tv);
        mWebsite = view.findViewById(R.id.card_details_website_tv);

        mEditBtn = view.findViewById(R.id.card_details_edit_btn);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_cardDetailsFragment_to_editCardDetailsFragment);
            }
        });

        return view;
    }

}
