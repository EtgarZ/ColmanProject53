package com.cardreaderapp.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cardreaderapp.R;
import com.cardreaderapp.models.User;
import com.squareup.picasso.Picasso;

import java.util.Vector;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserRowViewHolder> {
    public static Vector<User> mData;
    UsersListAdapter.OnItemClickListener mListener;

    public UsersListAdapter(Vector<User> data) {
        mData = data;
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
        User user = mData.elementAt(i);
        userRowViewHolder.bind(user);
    }

    @Override
    public int getItemCount() {
        return mData.size();
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
