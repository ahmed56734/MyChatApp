package com.example.ahmed.mychatapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.ahmed.mychatapp.ChatActivity;
import com.example.ahmed.mychatapp.R;

/**
 * Implementation of App Widget functionality.
 */
public class FriendsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String currentUserUid) {


        RemoteViews views ;
        // Construct the RemoteViews object
        if(currentUserUid != null) {
            views = new RemoteViews(context.getPackageName(), R.layout.favorite_friends_widget);

            Intent friendsRemoteViewsServiceIntent = new Intent(context, FriendsRemoteViewsService.class);
            friendsRemoteViewsServiceIntent.putExtra("userUid", currentUserUid);
            friendsRemoteViewsServiceIntent.putExtra("widgetId", appWidgetId);
            views.setRemoteAdapter(R.id.lv_friends_list, friendsRemoteViewsServiceIntent);
            views.setEmptyView(R.id.lv_friends_list, R.id.empty_view);

            Intent intent = new Intent(context, ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.lv_friends_list, pendingIntent);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_friends_list);
        }


        else{
            views = new RemoteViews(context.getPackageName(), R.layout.not_signed_in_widget);
        }


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        UpdateWidgetService.startActionUpdateFriendsWidget(context);

    }


    public static void updateFriendsWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String currentUserUid){

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, currentUserUid);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


}

