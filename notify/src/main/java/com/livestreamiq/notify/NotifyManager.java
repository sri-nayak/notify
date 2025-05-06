package com.livestreamiq.notify;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotifyManager {
    private static final String TAG = "NotifyManager";
    private static NotifyManager instance;
    
    private final Context context;
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationDesign notificationDesign;
    private TokenCallback tokenCallback;
    
    public interface TokenCallback {
        void onTokenReceived(String token);
        void onTokenError(Exception e);
    }
    
    private NotifyManager(Context context) {
        this.context = context.getApplicationContext();
        this.firebaseMessaging = FirebaseMessaging.getInstance();
        this.notificationDesign = new NotificationDesign(context);
    }
    
    public static synchronized NotifyManager getInstance(Context context) {
        if (instance == null) {
            instance = new NotifyManager(context);
        }
        return instance;
    }
    
    /**
     * Start listening for Firebase messages
     */
    public void startListening() {
        // The service will automatically start when the app starts
        Log.d(TAG, "Firebase Messaging Service is active");
    }

    /**
     * Get the current FCM token
     * @param callback Callback to receive the token
     */
    public void getToken(TokenCallback callback) {
        this.tokenCallback = callback;
        firebaseMessaging.getToken()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);
                    if (tokenCallback != null) {
                        tokenCallback.onTokenReceived(token);
                    }
                } else {
                    Log.e(TAG, "Failed to get FCM token", task.getException());
                    if (tokenCallback != null) {
                        tokenCallback.onTokenError(task.getException());
                    }
                }
            });
    }

    /**
     * Subscribe to a topic
     * @param topic Topic to subscribe to
     * @return CompletableFuture that completes when subscription is done
     */
    public CompletableFuture<Void> subscribeToTopic(String topic) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseMessaging.subscribeToTopic(topic)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Subscribed to topic: " + topic);
                    future.complete(null);
                } else {
                    Log.e(TAG, "Failed to subscribe to topic: " + topic, task.getException());
                    future.completeExceptionally(task.getException());
                }
            });
        return future;
    }

    /**
     * Unsubscribe from a topic
     * @param topic Topic to unsubscribe from
     * @return CompletableFuture that completes when unsubscription is done
     */
    public CompletableFuture<Void> unsubscribeFromTopic(String topic) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        firebaseMessaging.unsubscribeFromTopic(topic)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Unsubscribed from topic: " + topic);
                    future.complete(null);
                } else {
                    Log.e(TAG, "Failed to unsubscribe from topic: " + topic, task.getException());
                    future.completeExceptionally(task.getException());
                }
            });
        return future;
    }

    /**
     * Send a message to a specific topic
     * @param topic Topic to send the message to
     * @param title Message title
     * @param body Message body
     * @param data Additional data to send with the message
     * @return CompletableFuture that completes when the message is sent
     */
    public CompletableFuture<Void> sendMessageToTopic(String topic, String title, String body, Map<String, String> data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        // This is a placeholder. In a real implementation, you would need to:
        // 1. Have a server that can send FCM messages
        // 2. Make an API call to your server to send the message
        // 3. The server would then use the Firebase Admin SDK to send the message
        
        Log.d(TAG, "Message would be sent to topic: " + topic);
        future.complete(null);
        
        return future;
    }

    /**
     * Send a message to a specific device using its token
     * @param token Device token
     * @param title Message title
     * @param body Message body
     * @param data Additional data to send with the message
     * @return CompletableFuture that completes when the message is sent
     */
    public CompletableFuture<Void> sendMessageToDevice(String token, String title, String body, Map<String, String> data) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        
        // This is a placeholder. In a real implementation, you would need to:
        // 1. Have a server that can send FCM messages
        // 2. Make an API call to your server to send the message
        // 3. The server would then use the Firebase Admin SDK to send the message
        
        Log.d(TAG, "Message would be sent to device with token: " + token);
        future.complete(null);
        
        return future;
    }

    public void handleMessage(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        
        if (data.containsKey("design")) {
            try {
                JSONObject designConfig = new JSONObject(data.get("design"));
                notificationDesign.applyDesign(designConfig, new NotificationDesign.DesignCallback() {
                    @Override
                    public void onDesignApplied(NotificationCompat.Builder builder) {
                        // Add notification content
                        if (remoteMessage.getNotification() != null) {
                            builder.setContentTitle(remoteMessage.getNotification().getTitle())
                                  .setContentText(remoteMessage.getNotification().getBody());
                        }
                        
                        // Show the notification with permission check
                        showNotification(builder);
                    }
                    
                    @Override
                    public void onDesignError(Exception e) {
                        Log.e(TAG, "Error applying design", e);
                        // Fallback to default notification
                        showDefaultNotification(remoteMessage);
                    }
                });
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing design config", e);
                showDefaultNotification(remoteMessage);
            }
        } else {
            showDefaultNotification(remoteMessage);
        }
    }
    
    private void showDefaultNotification(RemoteMessage remoteMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(remoteMessage.getNotification().getTitle())
            .setContentText(remoteMessage.getNotification().getBody())
            .setAutoCancel(true);
            
        showNotification(builder);
    }

    private void showNotification(NotificationCompat.Builder builder) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check for notification permission
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                == PackageManager.PERMISSION_GRANTED) {
            try {
                notificationManager.notify(0, builder.build());
            } catch (SecurityException e) {
                Log.e(TAG, "SecurityException when showing notification", e);
            }
        } else {
            Log.w(TAG, "Notification permission not granted");
        }
    }

    public void clearDesignCache() {
        notificationDesign.clearCache();
    }
} 