package com.cardreaderapp.activities;


import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.adapters.CardsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Map;

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
    private ImageView mImageView;

    private Uri mImageUri;

    private Button mEditBtn;
    private Button mDeleteBtn;

    private DatabaseReference mDatabaseRef;
    private String mUserId;
    public CardDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_card_details, container, false);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/" +mUserId);

        final String name =  CardDetailsFragmentArgs.fromBundle(getArguments()).getName();
        final String phone =  CardDetailsFragmentArgs.fromBundle(getArguments()).getPhone();
        final String company =  CardDetailsFragmentArgs.fromBundle(getArguments()).getCompany();
        final String address =  CardDetailsFragmentArgs.fromBundle(getArguments()).getAddress();
        final String email =  CardDetailsFragmentArgs.fromBundle(getArguments()).getEmail();
        final String website =  CardDetailsFragmentArgs.fromBundle(getArguments()).getWebsite();
        mImageUri = CardDetailsFragmentArgs.fromBundle(getArguments()).getImageUri();

        mName = view.findViewById(R.id.card_details_name_tv);
        mPhone = view.findViewById(R.id.card_details_phone_tv);
        mCompany = view.findViewById(R.id.card_details_company_tv);
        mAddress = view.findViewById(R.id.card_details_address_tv);
        mEmail = view.findViewById(R.id.card_details_email_tv);
        mWebsite = view.findViewById(R.id.card_details_website_tv);
        mImageView = view.findViewById(R.id.card_details_imageView);

        mEditBtn = view.findViewById(R.id.card_details_edit_btn);
        mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigation.findNavController(v).navigate(R.id.action_cardDetailsFragment_to_editCardDetailsFragment);
                CardDetailsFragmentDirections.ActionCardDetailsFragmentToEditCardDetailsFragment action =
                        CardDetailsFragmentDirections.actionCardDetailsFragmentToEditCardDetailsFragment(
                                name,
                                phone,
                                company,
                                address,
                                email,
                                website,
                                mImageUri,
                                false
                        );
                Navigation.findNavController(v).navigate(action);
            }
        });

        mDeleteBtn = view.findViewById(R.id.card_details_delete_btn);
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseRef.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of cards in datasnapshot
                                Map<String,Object> cards = (Map<String,Object>) dataSnapshot.getValue();
                                for (Map.Entry<String, Object> entry : cards.entrySet()){

                                    //Get card map
                                    Map card = (Map) entry.getValue();
                                    if (card.get("mImageUri").toString() == mImageUri.toString()){
                                        String cardId = entry.getKey();
                                        mDatabaseRef.child(cardId).removeValue();
                                        Toast.makeText(view.getContext(), "Card deleted successfully!", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(view).navigate(R.id.action_cardDetailsFragment_to_cardsListFragment);
                                        return;
                                    }
                                }
                                Toast.makeText(getActivity(), "Couldn't delete card..", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
            }
        });

        mName.setText(name);
        mPhone.setText(phone);
        mCompany.setText(company);
        mAddress.setText(address);
        mEmail.setText(email);
        mWebsite.setText(website);
        Picasso.with(this.getContext()).load(mImageUri).fit().centerCrop().into(mImageView);

        return view;
    }

}
