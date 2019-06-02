package com.cardreaderapp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cardreaderapp.R;
import com.cardreaderapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.Vector;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserRowViewHolder> implements Filterable {
    public static Vector<User> mData;
    public static Vector<User> mfilterdData;

    UsersListAdapter.OnItemClickListener mListener;

    public UsersListAdapter(Vector<User> data) {
        mData = data;
        mfilterdData=data;
    }

    @Override
    public Filter getFilter() {
       return  new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString=constraint.toString();
                if(charString.isEmpty())
                {
                    mfilterdData=mData;
                }
                else
                {
                    Vector<User> filtering =new Vector<>();
                    for (User user: mData ) {
                        if(user.getName().toLowerCase().contains(charString))
                            filtering.add(user);
                    }
                    mfilterdData=filtering;
                }
                FilterResults results=new FilterResults();
                results.values=mfilterdData;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mfilterdData=(Vector<User>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnItemClickListener{
        void onClick(int index);
    }
    public void setOnItemClickListener(UsersListAdapter.OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public UsersListAdapter.UserRowViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.user_row, viewGroup,false);
        UsersListAdapter.UserRowViewHolder viewHolder = new UsersListAdapter.UserRowViewHolder(view,mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersListAdapter.UserRowViewHolder userRowViewHolder, int i) {
        User user = mfilterdData.elementAt(i);
        userRowViewHolder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mfilterdData.size();
    }

    class UserRowViewHolder extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mName;
        public UserRowViewHolder(@NonNull final View itemView,
                                 final UsersListAdapter.OnItemClickListener listener) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.user_row_avatar_img);
            mName = itemView.findViewById(R.id.user_row_name_tv);

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

        public void bind(User user){
            mName.setText(user.getName());
            if (user.getImageUri() != null)
                Picasso.with(itemView.getContext()).load(user.getImageUri()).fit().into(mAvatar);
            else
                mAvatar.setImageResource(R.drawable.user_default_image);
        }
    }
}
