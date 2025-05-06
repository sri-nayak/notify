package com.livestreamiq.notify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.Manifest;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.util.Map;

public class NotifyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "NotifyFCMService";
    private static final String CHANNEL_ID = "notify_channel";
    private static final String CHANNEL_NAME = "Notify Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel for Notify library notifications";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        // Initialize the library when the service starts
        NotifyManager.getInstance(getApplicationContext()).startListening();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage);
        }

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotificationMessage(remoteMessage);
        }
    }

    private void handleDataMessage(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        
        // Create an intent to launch the app
        Intent intent = new Intent(this, getApplicationContext().getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Add data to intent extras
        Bundle extras = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            extras.putString(entry.getKey(), entry.getValue());
        }
        intent.putExtras(extras);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Handle dynamic design if present
        if (data.containsKey("design")) {
            try {
                NotifyManager.getInstance(this).handleMessage(remoteMessage);
            } catch (Exception e) {
                Log.e(TAG, "Error handling dynamic design", e);
                showDefaultNotification(remoteMessage, pendingIntent);
            }
        } else {
            showDefaultNotification(remoteMessage, pendingIntent);
        }
    }

    private void handleNotificationMessage(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            // Create an intent to launch the app
            Intent intent = new Intent(this, getApplicationContext().getClass());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

            showDefaultNotification(remoteMessage, pendingIntent);
        }
    }

    private void showDefaultNotification(RemoteMessage remoteMessage, PendingIntent pendingIntent) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification == null) return;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Add big text style if body is long
        if (notification.getBody() != null && notification.getBody().length() > 50) {
            notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(notification.getBody()));
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        
        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED) {
            try {
                notificationManager.notify(0, notificationBuilder.build());
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException when showing notification", e);
            }
        } else {
            Log.w(TAG, "Notification permission not granted");
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        
        // Notify the manager about the new token
        NotifyManager.getInstance(getApplicationContext()).getToken(new NotifyManager.TokenCallback() {
            @Override
            public void onTokenReceived(String newToken) {
                // Token is already updated in the manager
            }

            @Override
            public void onTokenError(Exception e) {
                Log.e(TAG, "Failed to update token", e);
            }
        });
    }
} 