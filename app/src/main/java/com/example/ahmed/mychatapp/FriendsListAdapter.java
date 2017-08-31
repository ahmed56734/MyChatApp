package com.example.ahmed.mychatapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ahmed on 8/22/17.
 */

public class FriendsListAdapter extends RecyclerView.Adapter {

    private List<User> mFriendsList;
    private OnFriendClickListener mOnFriendClickListener;


    interface OnFriendClickListener{
        void onClick(User friend);
    }

    FriendsListAdapter(OnFriendClickListener onFriendClickListener){
        mFriendsList = new ArrayList<>();
        this.mOnFriendClickListener = onFriendClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_list_item, parent, false);
        return new FriendItemViewHolder(view);
    }





    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = mFriendsList.get(position);
        ((FriendItemViewHolder)holder).setFriendNameTextView(user.getName());
        ((FriendItemViewHolder)holder).setFriendIconImageView(user.getPhotoUrl());
    }

    public void addFriend(User user){
        mFriendsList.add(user);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    class FriendItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_friend_icon)
        ImageView friendIconImageView;
        @BindView(R.id.tv_display_name)
        TextView friendNameTextView;


        public FriendItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnFriendClickListener.onClick(mFriendsList.get(getAdapterPosition()));
                }
            });
        }

         void setFriendIconImageView(String imageUrl){
             if(imageUrl != null) {
                 Picasso.with(itemView.getContext()).load(imageUrl).resize(50, 50).centerInside().into(friendIconImageView);
             }

        }

         void setFriendNameTextView(String name){
            friendNameTextView.setText(name);
        }
    }


}
