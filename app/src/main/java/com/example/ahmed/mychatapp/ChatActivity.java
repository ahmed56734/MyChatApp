package com.example.ahmed.mychatapp;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatActivity extends AppCompatActivity {

    private User mFriend;
    private String mCurrentUserUid;
    private String mChatId;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatReference;

    @BindView(R.id.rv_chat_messages)
    RecyclerView mChatRecyclerView;
    @BindView(R.id.sendButton)
    ImageButton mSendMessageImageButton;
    @BindView(R.id.messageEditText)
    EditText mMessageEditText;

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



        mSendMessageImageButton.setOnClickListener(new SendButtonClickListener());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mChatRecyclerView.setLayoutManager(linearLayoutManager);




        mChatAdapter = new ChatMessagesAdapter(Message.class, R.layout.chat_message_item, mChatReference, mFriend.getUid(), mFriend.getPhotoUrl());





    }

    @Override
    protected void onStart() {
        super.onStart();
        mChatRecyclerView.setAdapter(mChatAdapter);
    }

    private String getChatId(String userUid, String friendUid){
        String chatId;

        if(userUid.compareTo(friendUid) < 0)
            chatId = userUid + friendUid;
        else
            chatId = friendUid + userUid;

        return chatId;
    }




    private class SendButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String messageText = mMessageEditText.getText().toString().trim();

            if(!messageText.isEmpty())
                sendMessage(messageText);

        }

         private void sendMessage(String message){

             Message myMessage = new Message(mCurrentUserUid, message);

             mChatReference.push().setValue(myMessage).addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful())
                         mMessageEditText.setText("");
                     else{
                         Toast.makeText(getApplicationContext(), "error happened sending your message", Toast.LENGTH_SHORT).show();
                         Log.e(LOG_TAG, "error sending message", task.getException());
                     }
                 }
             });
         }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatAdapter.cleanup();
    }
}
