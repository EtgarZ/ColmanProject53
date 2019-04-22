package com.cardreaderapp.activities;


import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.ContactsContract;
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
import com.cardreaderapp.models.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
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
    private Button mExportBtn;
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

        mExportBtn = view.findViewById(R.id.card_details_export_btn);
        mExportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openContact(new Card(name,phone,company,address,email,website));
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

    private void openContact(Card card)
    {
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
    }

}
