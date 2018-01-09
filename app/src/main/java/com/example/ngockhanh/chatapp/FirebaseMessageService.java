package com.example.ngockhanh.chatapp;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Ngoc Khanh on 1/8/2018.
 */

public class FirebaseMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String notification_title=remoteMessage.getNotification().getTitle();
        String notification_body=remoteMessage.getNotification().getBody();
        String notification_click_action=remoteMessage.getNotification().getClickAction();
        /*Get Data from message send*/
        /*Get User id sended*/
        String from_user_id=remoteMessage.getData().get("from_user_id");


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle(notification_title)
                        .setContentText(notification_body);
        Intent resultIntent=new Intent(notification_click_action);
        resultIntent.putExtra("user_id",from_user_id);
        PendingIntent resultPendingIntent=PendingIntent.getActivity(this,0,resultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = (int)System.currentTimeMillis();
    // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }
}
