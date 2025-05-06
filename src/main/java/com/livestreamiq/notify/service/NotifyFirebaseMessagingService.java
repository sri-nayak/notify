package com.livestreamiq.notify.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.livestreamiq.notify.R;
import com.livestreamiq.notify.model.NotificationPayload;

import java.util.Map;

public class NotifyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "NotifyFCMService";
    private static final String CHANNEL_ID = "notify_channel";
    private static final String CHANNEL_NAME = "Notify Channel";
    private static final String CHANNEL_DESCRIPTION = "Channel for Notify library notifications";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Handle FCM messages here
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }

        // Check if message contains a notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotificationMessage(remoteMessage);
        }
    }

    private void handleDataMessage(Map<String, String> data) {
        try {
            Gson gson = new Gson();
            String jsonData = gson.toJson(data);
            NotificationPayload payload = gson.fromJson(jsonData, NotificationPayload.class);
            
            // If template_id is present, fetch template from server
            if (payload.getTemplateId() != null) {
                fetchTemplateAndShowNotification(payload);
            } else {
                showNotification(payload);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing notification data: " + e.getMessage());
        }
    }

    private void handleNotificationMessage(RemoteMessage remoteMessage) {
        NotificationPayload payload = new NotificationPayload();
        payload.setTitle(remoteMessage.getNotification().getTitle());
        payload.setBody(remoteMessage.getNotification().getBody());
        payload.setImageUrl(remoteMessage.getNotification().getImageUrl() != null ? 
            remoteMessage.getNotification().getImageUrl().toString() : null);
        
        showNotification(payload);
    }

    private void fetchTemplateAndShowNotification(NotificationPayload payload) {
        // TODO: Implement template fetching from server
        // For now, we'll just show the notification with the basic data
        showNotification(payload);
    }

    private void showNotification(NotificationPayload payload) {
        Intent intent = new Intent(this, getApplicationContext().getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(payload.getTitle())
                .setContentText(payload.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (payload.getImageUrl() != null) {
            // TODO: Load image using Glide or Picasso
            // For now, we'll just show the basic notification
        }

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the NotificationChannel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);
        // TODO: Implement token refresh handling
    }
} 