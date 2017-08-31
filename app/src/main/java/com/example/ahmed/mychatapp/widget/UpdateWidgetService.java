package com.example.ahmed.mychatapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.ahmed.mychatapp.User;
import com.example.ahmed.mychatapp.Utils;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
