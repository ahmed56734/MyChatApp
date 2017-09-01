package com.example.ahmed.mychatapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ahmed on 8/24/17.
 */

public class ChatMessagesAdapter extends FirebaseRecyclerAdapter<Message, ChatMessagesAdapter.MessageViewHolder> {


    private  String mFriendUid;
    private  String mFriendImageUrl;

    private String foo;

    public ChatMessagesAdapter(Class<Message> modelClass, int modelLayout, Query ref, String friendUid, String friendImageUrl) {
        super(modelClass, modelLayout, MessageViewHolder.class, ref);

        mFriendUid = friendUid;
        mFriendImageUrl = friendImageUrl;

    }

    @Override
    protected void populateViewHolder(MessageViewHolder viewHolder, Message model, int position) {
        viewHolder.setMessageText(model, mFriendUid);
        viewHolder.setFriendIconImageView(mFriendUid, mFriendImageUrl);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_friend_icon)
        ImageView friendIconImageView;
        @BindView(R.id.messageTextView)
        TextView messageTextView;
        @BindView(R.id.left_empty_view)
        View leftView;
        @BindView(R.id.right_empty_view)
        View rightView;

        public MessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setFriendIconImageView(String friendUid, String friendImageUrl) {
            if (friendImageUrl != null && !friendImageUrl.isEmpty())
                Picasso.with(itemView.getContext()).load(friendImageUrl).resize(24, 24).centerInside().into(friendIconImageView);
        }

        void setMessageText(Message message, String friendUid) {

            messageTextView.setText(message.getMessage());
            Context context = messageTextView.getContext();

            if (message.getSender().equals(friendUid)) {

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.START);
                leftView.setVisibility(View.GONE);
                rightView.setVisibility(View.VISIBLE);
                friendIconImageView.setVisibility(View.VISIBLE);
                messageTextView.setLayoutParams(params);
                messageTextView.setBackground(context.getDrawable(R.drawable.friend_message_drawable));
                messageTextView.setTextColor(context.getResources().getColor(android.R.color.black));



            } else {
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.END);
                leftView.setVisibility(View.VISIBLE);
                rightView.setVisibility(View.GONE);
                friendIconImageView.setVisibility(View.GONE);
                messageTextView.setLayoutParams(params);
                messageTextView.setBackground(context.getDrawable(R.drawable.user_message_drawable));
                messageTextView.setTextColor(context.getResources().getColor(android.R.color.white));
            }
        }
    }

}
