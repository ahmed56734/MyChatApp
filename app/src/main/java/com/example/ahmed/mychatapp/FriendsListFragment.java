package com.example.ahmed.mychatapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsListFragment extends Fragment {

    private String mCurrentUserUid;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCurrentUserFriends;
    @BindView(R.id.rv_friends_list)
    RecyclerView mFriendsListRecyclerView;
    FriendsListAdapter mFriendsListAdapter;

    public FriendsListFragment() {
        // Required empty public constructor
    }

//    public static FriendsListFragment newInstance(String currentUserUid) {
//
//        Bundle args = new Bundle();
//        args.putString("uid", currentUserUid);
//
//        FriendsListFragment fragment = new FriendsListFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        mFriendsListAdapter = new FriendsListAdapter(new FriendsListAdapter.OnFriendClickListener() {
            @Override
            public void onClick(User friend) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("friend", friend);
                intent.putExtra("currentUserUid", mCurrentUserUid);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  view =  inflater.inflate(R.layout.fragment_friends_list, container, false);
        ButterKnife.bind(this, view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mFriendsListRecyclerView.setLayoutManager(linearLayoutManager);

        mFriendsListRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCurrentUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mCurrentUserFriends = mFirebaseDatabase.getReference().child("friends").child(mCurrentUserUid);
        mFriendsListAdapter = new FriendsListAdapter(new FriendsListAdapter.OnFriendClickListener() {
            @Override
            public void onClick(User friend) {
                Intent intent = new Intent(getContext(), ChatActivity.class);
                intent.putExtra("friend", friend);
                intent.putExtra("currentUserUid", mCurrentUserUid);
                startActivity(intent);
            }
        });

        mFriendsListRecyclerView.setAdapter(mFriendsListAdapter);

        mCurrentUserFriends.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String friendUid = dataSnapshot.getKey();
                DatabaseReference databaseReference = mFirebaseDatabase.getReference().child("users").child(friendUid);
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User friend = dataSnapshot.getValue(User.class);
                        mFriendsListAdapter.addFriend(friend);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
