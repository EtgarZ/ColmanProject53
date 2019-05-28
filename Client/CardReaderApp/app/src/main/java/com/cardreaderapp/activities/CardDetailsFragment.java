package com.cardreaderapp.activities;


import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cardreaderapp.R;
import com.cardreaderapp.models.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardDetailsFragment extends Fragment  {

    private TextView mName;
    private TextView mPhone;
    private TextView mCompany;
    private TextView mAddress;
    private TextView mEmail;
    private TextView mWebsite;
    private ImageView mImageView;

    private String name;
    private String phone;
    private String company;
    private String address;
    private String email;
    private String website;
    private Uri mImageUri;

    private DatabaseReference mDatabaseRef;
    private String mUserId;
    public CardDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.menu_card_details, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.card_details_edit_item:
                //do sth here
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
                Navigation.findNavController(getView()).navigate(action);
                return true;
            case R.id.card_details_delete_item:
                //do sth here
                openDeleteCardDialog();
                return true;

            case R.id.card_details_add_contact_item:
                //do sth here
                openContact(new Card(name,phone,company,address,email,website));
                return true;
        }
        return false;
    }

    private DialogInterface.OnClickListener createDeleteDialog()
    {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        deleteCard();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
    }

    private void openDeleteCardDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = createDeleteDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());
        builder.setMessage("You are about to delete card.\nConfirm to proceed")
                .setPositiveButton("Confirm", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    private void deleteCard()
    {
        mDatabaseRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of cards in datasnapshot
                        Map<String,Object> user = (Map<String,Object>) dataSnapshot.getValue();
                        Map<String,Object> cards = (Map<String,Object>) user.get("Cards");
                        for (Map.Entry<String, Object> entry : cards.entrySet()){

                            //Get card map
                            Map card = (Map) entry.getValue();
                            if (card.get("imageUri").toString().equals(mImageUri.toString())){
                                String cardId = entry.getKey();
                                mDatabaseRef.child("Cards").child(cardId).removeValue();
                                Toast.makeText(getView().getContext(), "Card deleted successfully!", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.action_cardDetailsFragment_to_cardsListFragment);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_card_details, container, false);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users/" +mUserId);

        name =  CardDetailsFragmentArgs.fromBundle(getArguments()).getName();
        phone =  CardDetailsFragmentArgs.fromBundle(getArguments()).getPhone();
        company =  CardDetailsFragmentArgs.fromBundle(getArguments()).getCompany();
        address =  CardDetailsFragmentArgs.fromBundle(getArguments()).getAddress();
        email =  CardDetailsFragmentArgs.fromBundle(getArguments()).getEmail();
        website =  CardDetailsFragmentArgs.fromBundle(getArguments()).getWebsite();
        mImageUri = CardDetailsFragmentArgs.fromBundle(getArguments()).getImageUri();

        mName = view.findViewById(R.id.card_details_name_tv);
        mPhone = view.findViewById(R.id.card_details_phone_tv);
        mCompany = view.findViewById(R.id.card_details_company_tv);
        mAddress = view.findViewById(R.id.card_details_address_tv);
        mEmail = view.findViewById(R.id.card_details_email_tv);
        mWebsite = view.findViewById(R.id.card_details_website_tv);
        mImageView = view.findViewById(R.id.card_details_imageView);

        mName.setText(name);
        mPhone.setText(phone);
        mCompany.setText(company);
        mAddress.setText(address);
        mEmail.setText(email);
        mWebsite.setText(website);
        File f = new File(mImageUri.toString());
        Picasso.with(this.getContext()).load(f).fit().into(mImageView);
        return view;
    }

    private void openContact(Card card)
    {
        // Create intent contact-add
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        // Send card data to contact intent
        intent.putExtra(ContactsContract.Intents.Insert.NAME, card.getPersonName())
                .putExtra(ContactsContract.Intents.Insert.PHONE, card.getPhoneNumber())
                .putExtra(ContactsContract.Intents.Insert.PHONE_TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, card.getEmail())
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE,
                        ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.COMPANY, card.getCompany())
                .putExtra(ContactsContract.Intents.Insert.POSTAL, card.getAddress())
                .putExtra(ContactsContract.Intents.Insert.POSTAL_TYPE,
                        ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);

        ArrayList<ContentValues> data = new ArrayList<ContentValues>();

        ContentValues row1 = new ContentValues();
        row1.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
        row1.put(ContactsContract.CommonDataKinds.Website.DATA1, card.getWebsite());
        data.add(row1);

        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        // Open contact member and fill details from card
        startActivity(intent);
    }


}
