package com.example.ahmed.mychatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import static com.example.ahmed.mychatapp.Utils.checkIfExistingUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUsersReference;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new MyAuthStateListener();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mUsersReference = mFirebaseDatabase.getReference().child("users");

    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);


    }




    private class MyAuthStateListener implements FirebaseAuth.AuthStateListener {

        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

            final FirebaseUser user = firebaseAuth.getCurrentUser();

            if (user != null) {

                checkIfExistingUser(user.getUid(), new Utils.IsExistingUserCallback() {
                    @Override
                    public void onResult(boolean isExistingUser) {
                        if (!isExistingUser) {

                            addNewUser(user);

                        } else {
                            Toast.makeText(getApplicationContext(), "he isn't new user", Toast.LENGTH_SHORT).show();
                            Utils.setActive(user.getUid(), true);
                        }
                    }


                });

                //start main activity
                startActivity(new Intent(getApplicationContext(), MainActivity.class));


            } else {
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setProviders(
                                        AuthUI.GOOGLE_PROVIDER,
                                        AuthUI.FACEBOOK_PROVIDER,
                                        AuthUI.EMAIL_PROVIDER)
                                .setLogo(R.drawable.logo)
                                .build(),
                        RC_SIGN_IN);
            }
        }
    }


    private void addNewUser(final FirebaseUser user) {

        User myUser = new User(user.getUid(), user.getDisplayName(), user.getEmail());
        if (user.getPhotoUrl() != null)
            myUser.setPhotoUrl(user.getPhotoUrl().toString());


        mUsersReference.child(myUser.getUid()).setValue(myUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Utils.setActive(user.getUid(), true);
            }
        });

        Toast.makeText(getApplicationContext(), "new user added to database", Toast.LENGTH_SHORT).show();

    }


}
