package com.example.ahmed.mychatapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ahmed.mychatapp.widget.UpdateWidgetService;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    private User mFriend;
    private String mCurrentUserUid;
    private String mChatId;
    private boolean mIsFavoriteFriend = false;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatReference;
    DatabaseReference mFavoritesDatabaseReference ;

    @BindView(R.id.rv_chat_messages)
    RecyclerView mChatRecyclerView;
    @BindView(R.id.sendButton)
    ImageButton mSendMessageImageButton;
    @BindView(R.id.messageEditText)
    EditText mMessageEditText;

    private ImageButton mFavoriteImageButton;
    private TextView mFriendNameTextView;



    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private FirebaseRecyclerAdapter mChatAdapter;
    String LOG_TAG = ChatActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        mFriend = bundle.getParcelable("friend");
        mCurrentUserUid = bundle.getString("currentUserUid");

        mChatId = getChatId(mCurrentUserUid, mFriend.getUid());

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mChatReference = mFirebaseDatabase.getReference().child("chat").child(mChatId);
        mFavoritesDatabaseReference = mFirebaseDatabase.getReference().child("favorites").child(mCurrentUserUid).child(mFriend.getUid());

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflator.inflate(R.layout.chat_custom_bar, null);
        mFavoriteImageButton = (ImageButton) view.findViewById(R.id.favorite_btn);
        prepareFavoriteButton();
        mFriendNameTextView = (TextView) view.findViewById(R.id.tv_friend_name) ;
        mFriendNameTextView.setText(mFriend.getName());


        actionBar.setCustomView(view);








        mSendMessageImageButton.setOnClickListener(new SendButtonClickListener());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mChatRecyclerView.setLayoutManager(linearLayoutManager);


        mChatAdapter = new ChatMessagesAdapter(Message.class, R.layout.chat_message_item, mChatReference, mFriend.getUid(), mFriend.getPhotoUrl());


    }


    @Override
    protected void onStart() {
        super.onStart();
        mFavoriteImageButton.setOnClickListener(new FavoriteButtonClickListener());
        mChatRecyclerView.setAdapter(mChatAdapter);

    }

    private String getChatId(String userUid, String friendUid) {
        String chatId;

        if (userUid.compareTo(friendUid) < 0)
            chatId = userUid + friendUid;
        else
            chatId = friendUid + userUid;

        return chatId;
    }


    private class SendButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            String messageText = mMessageEditText.getText().toString().trim();

            if (!messageText.isEmpty())
                sendMessage(messageText);

        }

        private void sendMessage(String message) {

            Message myMessage = new Message(mCurrentUserUid, message);

            mChatReference.push().setValue(myMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        mMessageEditText.setText("");
                    else {
                        Toast.makeText(getApplicationContext(), "error happened sending your message", Toast.LENGTH_SHORT).show();
                        Log.e(LOG_TAG, "error sending message", task.getException());
                    }
                }
            });
        }
    }

    private class FavoriteButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            mFavoriteImageButton.setEnabled(false);

            if(mIsFavoriteFriend){
                mFavoritesDatabaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mFavoriteImageButton.setEnabled(true);
                        UpdateWidgetService.startActionUpdateFriendsWidget(getApplicationContext());
                    }
                });
            }

            else{

                mFavoritesDatabaseReference.setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mFavoriteImageButton.setEnabled(true);
                        UpdateWidgetService.startActionUpdateFriendsWidget(getApplicationContext());
                    }
                });
            }

        }
    }


    private void prepareFavoriteButton(){


        mFavoritesDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mFavoriteImageButton.setImageResource(android.R.drawable.star_big_on);
                    mIsFavoriteFriend = true;

                }
                else{
                    mFavoriteImageButton.setImageResource(android.R.drawable.star_big_off);
                    mIsFavoriteFriend = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatAdapter.cleanup();

    }
}
