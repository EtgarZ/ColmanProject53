package com.cardreaderapp.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardreaderapp.R;
import com.cardreaderapp.models.Card;
import com.squareup.picasso.Picasso;

import java.util.Vector;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.CardRowViewHolder> implements Filterable {
    public static Vector<Card> mData;
    public static Vector<Card> filtered;
    OnItemClickListener mListener;

    public CardsListAdapter(Vector<Card> data) {
        mData = data;
        filtered=data;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    filtered = mData;
                } else {
                    Vector<Card> filteredList = new Vector<Card>();
                    for (Card row : mData) {
                        if (row.getPersonName().toLowerCase().contains(charString.toLowerCase()) || row.getPhoneNumber().contains(charString)) {
                            filteredList.add(row);
                        }
                    }
                    filtered = filteredList;

                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filtered;
                return filterResults;
            }

                @Override
                protected void publishResults (CharSequence constraint, FilterResults results){
                    filtered = (Vector<Card>) results.values;
                    notifyDataSetChanged();
                }


        };
    }



    public interface OnItemClickListener{
        void onClick(int index);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public CardRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_row, viewGroup,false);
        CardRowViewHolder viewHolder = new CardRowViewHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardRowViewHolder cardRowViewHolder, int i) {
        Card card = filtered.elementAt(i);
        cardRowViewHolder.bind(card);
    }

    @Override
    public int getItemCount() {
        return filtered.size();
    }

    class CardRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mName;
        TextView mPhone;
        View mView;
        public CardRowViewHolder(@NonNull final View itemView,
                                    final OnItemClickListener listener) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.strow_avatar_img);
            mName = itemView.findViewById(R.id.card_row_name_tv);
            mPhone = itemView.findViewById(R.id.card_row_phone_tv);
            mView=itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = getAdapterPosition();
                    if (listener != null){
                        if (index != RecyclerView.NO_POSITION) {
                            listener.onClick(index);
                        }
                    }
                }
            });
        }

        public void bind(Card card){
            mName.setText(card.getPersonName());
            mPhone.setText(card.getPhoneNumber());
            Picasso.with(itemView.getContext()).load(card.getImageUri())
                    .fit().centerCrop().into(mAvatar);
        }
    }
}