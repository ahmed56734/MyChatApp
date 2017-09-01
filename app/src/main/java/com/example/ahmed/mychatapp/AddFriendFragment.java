package com.example.ahmed.mychatapp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.ahmed.mychatapp.Utils.addNewFriend;
import static com.example.ahmed.mychatapp.Utils.checkIfExistingUserByEmail;
import static com.example.ahmed.mychatapp.Utils.checkIfFriends;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddFriendFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentFirebaseUser;
    @BindView(R.id.ed_friend_email)
    TextInputEditText mFriendEmailEditText;
    @BindView(R.id.btn_add_friend)
    Button mAddFriendButton;
    private Context context;



    public AddFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this, view);

        mAddFriendButton.setOnClickListener(new AddFriend());
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        context = getContext();

    }

    @Override
    public void onStart() {
        super.onStart();
        mCurrentFirebaseUser = mAuth.getCurrentUser();
    }

    private class AddFriend implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            String email = mFriendEmailEditText.getText().toString().trim();

            if(email.isEmpty())
                Toast.makeText(context, context.getString(R.string.email_field_is_empty), Toast.LENGTH_SHORT).show();

            else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
                Toast.makeText(context, context.getString(R.string.insert_valid_email), Toast.LENGTH_SHORT).show();



            else{
                checkIfExistingUserByEmail(email, new Utils.IsExistingUserByEmailCallback() {
                    @Override
                    public void onResult(boolean isExistingUser, final String friendUid) {
                        if(isExistingUser){


                            checkIfFriends(mCurrentFirebaseUser.getUid(), friendUid, new Utils.AreFriendsCallbacks() {
                                @Override
                                public void onResult(boolean areFriends) {
                                    if(areFriends){
                                        Toast.makeText(context, context.getString(R.string.you_are_already_friends), Toast.LENGTH_SHORT).show();
                                    }
                                    else{

                                        addNewFriend(mCurrentFirebaseUser.getUid(), friendUid, getContext());
                                        mFriendEmailEditText.setText("");
                                    }
                                }
                            });
                        }

                        else {
                            Toast.makeText(context, context.getString(R.string.the_email_owner_isnt_an_app_user_yet), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, getContext());
            }

        }



    }
}
