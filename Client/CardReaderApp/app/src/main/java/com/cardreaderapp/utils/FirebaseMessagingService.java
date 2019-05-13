package com.cardreaderapp.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.cardreaderapp.R;
import com.cardreaderapp.activities.MainActivity;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    NotificationCompat.Builder builder;
    RemoteViews remoteViews;
    NotificationManager manager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        androidx.core.app.NotificationCompat.Builder builder =
                new androidx.core.app.NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));


        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String fromUserId = remoteMessage.getData().get("from_user_id");
        String toUserId = remoteMessage.getData().get("to_user_id");

        remoteViews = new RemoteViews(getPackageName(),R.layout.custom_notification);
        remoteViews.setImageViewResource(R.id.notif_icon,R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.notif_title, title + ":" + body);



        int notification_id = (int) System.currentTimeMillis();

        Intent button_intent = new Intent("button_click");
        button_intent.putExtra("notification_id",notification_id);
        button_intent.putExtra("from_user_id",fromUserId);
        button_intent.putExtra("to_user_id",toUserId);
        PendingIntent button_pending_event = PendingIntent.getBroadcast(getBaseContext(),notification_id,
                button_intent,PendingIntent.FLAG_CANCEL_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.button, button_pending_event);

        Intent notification_intent = new Intent(getBaseContext(),MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(),0,notification_intent,0);

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);

        manager.notify(notification_id,builder.build());
    }

//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        String title = remoteMessage.getNotification().getTitle();
//        String body = remoteMessage.getNotification().getBody();
//
//
//        androidx.core.app.NotificationCompat.Builder builder =
//                new androidx.core.app.NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle(title)
//                        .setContentText(body);
//
//        int notificationId = (int)System.currentTimeMillis();
//
//        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        manager.notify(notificationId, builder.build());
//    }
}
