package com.example.ahmed.mychatapp.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.ahmed.mychatapp.R;
import com.example.ahmed.mychatapp.User;
import com.example.ahmed.mychatapp.Utils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by ahmed on 8/27/17.
 */

public class FriendsRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Bundle bundle = intent.getExtras();
        String userUid = bundle.getString("userUid");
        int widgetId = bundle.getInt("widgetId");
        return new FriendsRemoteViewsFactory(getApplicationContext(), userUid, widgetId);
    }
}


class FriendsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private String mUserUid;
    private Context mContext;
    private int mWidgetId;
    private List<String> mFriendsList;

    FriendsRemoteViewsFactory(Context context, String userUid, int widgetId) {
        this.mUserUid = userUid;
        this.mContext = context;
        mWidgetId = widgetId;
    }



    @Override
    public void onCreate() {
        mFriendsList = new ArrayList<>();
    }

    @Override
    public void onDataSetChanged() {

        mFriendsList.clear();

        TaskCompletionSource<DataSnapshot> taskCompletionSource = new TaskCompletionSource<>();
        Task<DataSnapshot> task = taskCompletionSource.getTask();
        Utils.getFavoriteFriends(mUserUid, taskCompletionSource);
        DataSnapshot dataSnapshot = null;

        try {
            dataSnapshot = Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if(dataSnapshot != null && dataSnapshot.getChildrenCount() > 0){
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                mFriendsList.add(child.getKey());
            }

        }
        else
            Log.d("intent", "snapshot is null");

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {

        return mFriendsList.size();

    }

    @Override
    public RemoteViews getViewAt(int i) {

        final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.widget_friend_list_item);

        TaskCompletionSource<User> taskCompletionSource = new TaskCompletionSource<>();
        Task task = taskCompletionSource.getTask();
        Utils.getUser(mFriendsList.get(i), taskCompletionSource);
        User friend = null;
        try {
            friend = (User) Tasks.await(task);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if(friend != null) {

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra("friend", friend);
            fillInIntent.putExtra("currentUserUid", mUserUid);
            remoteViews.setOnClickFillInIntent(R.id.tv_display_name, fillInIntent);

            remoteViews.setTextViewText(R.id.tv_display_name, friend.getName());
            String photoUrl = friend.getPhotoUrl();
            if(photoUrl != null && !photoUrl.isEmpty()){

                try {
                    Bitmap bitmap =  Picasso.with(mContext).load(photoUrl).transform(new CropCircleTransformation()).get();
                    remoteViews.setImageViewBitmap(R.id.iv_friend_icon, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }





            }
        }



        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
