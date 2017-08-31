package com.example.ahmed.mychatapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.ahmed.mychatapp.widget.UpdateWidgetService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ahmed on 8/20/17.
 */

public class Utils {

    static void checkIfExistingUser(final String uid, final IsExistingUserCallback isExistingUserCallback) {


        DatabaseReference mUsersReference = FirebaseDatabase.getInstance().getReference().child("users");
        mUsersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(uid))
                    isExistingUserCallback.onResult(true);


                else
                    isExistingUserCallback.onResult(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    static void checkIfExistingUserByEmail(final String email, final IsExistingUserByEmailCallback isExistingUserByEmailCallback, final Context context) {


        Query query = FirebaseDatabase.getInstance().getReference().child("users").orderByChild("email").equalTo(email);
//
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1) {

                    for (DataSnapshot child : dataSnapshot.getChildren())
                        isExistingUserByEmailCallback.onResult(true, child.getKey());
                }

                else
                    isExistingUserByEmailCallback.onResult(false, null);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    static void checkIfFriends(String userUid, final String friendUid, final AreFriendsCallbacks areFriendsCallbacks) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(userUid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(friendUid)) {
                    areFriendsCallbacks.onResult(true);
                } else {
                    areFriendsCallbacks.onResult(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    static void addNewFriend(String userUid, String friendUid, final Context context) {
        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(userUid).child(friendUid);
        final DatabaseReference friendDatabaseReference = FirebaseDatabase.getInstance().getReference().child("friends").child(friendUid).child(userUid);
        final Map map = new HashMap();
        map.put("since", ServerValue.TIMESTAMP);
        userDatabaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    friendDatabaseReference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(context, "friend added", Toast.LENGTH_SHORT).show();
                            UpdateWidgetService.startActionUpdateFriendsWidget(context);
                        }
                    });
                } else
                    Toast.makeText(context, "error happened adding your friend", Toast.LENGTH_SHORT).show();

            }
        });


    }


    static void setActive(String uid, Boolean active) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
        Map map = new HashMap();
        map.put("online", active);
        databaseReference.updateChildren(map);
    }


    public static void getFavoriteFriends(final String currentUserUid, final TaskCompletionSource<DataSnapshot> taskCompletionSource) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("favorites").child(currentUserUid);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                taskCompletionSource.setResult(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void getUser(String userUid, final TaskCompletionSource<User> taskCompletionSource) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(userUid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                taskCompletionSource.setResult(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    interface IsExistingUserCallback {
        void onResult(boolean isExistingUser);
    }

    interface IsExistingUserByEmailCallback {
        void onResult(boolean isExistingUser, String uid);
    }

    interface AreFriendsCallbacks {
        void onResult(boolean areFriends);
    }

}
