package com.example.ahmed.mychatapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by ahmed on 8/27/17.
 */

public class UpdateWidgetService extends IntentService {
    public UpdateWidgetService() {
        super("UpdateWidgetService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String currentUserUid = null;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            currentUserUid = user.getUid();
        }

        AppWidgetManager appWidgetManager =  AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, FriendsWidgetProvider.class));
        FriendsWidgetProvider.updateFriendsWidget(this, appWidgetManager, appWidgetIds, currentUserUid);





    }

    public static void startActionUpdateFriendsWidget(Context context){
        Intent intent = new Intent(context, UpdateWidgetService.class);
        context.startService(intent);
    }
}
