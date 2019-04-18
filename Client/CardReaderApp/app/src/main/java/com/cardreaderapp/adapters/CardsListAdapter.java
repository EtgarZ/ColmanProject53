package com.cardreaderapp.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cardreaderapp.R;
import com.cardreaderapp.models.Card;

import java.util.Vector;

public class CardsListAdapter extends RecyclerView.Adapter<CardsListAdapter.CardRowViewHolder> {
    public static Vector<Card> mData;
    OnItemClickListener mListener;

    public CardsListAdapter(Vector<Card> data) {
        mData = data;
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
        Card card = mData.elementAt(i);
        cardRowViewHolder.bind(card);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class CardRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mName;
        TextView mPhone;

        public CardRowViewHolder(@NonNull final View itemView,
                                    final OnItemClickListener listener) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.strow_avatar_img);
            mName = itemView.findViewById(R.id.card_row_name_tv);
            mPhone = itemView.findViewById(R.id.card_row_phone_tv);
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
            mName.setText(card.GetPersonName());
            mPhone.setText("id: " + card.GetPhoneNumber());
        }
    }
}