# Notify Library Example

This example demonstrates how to use the Notify library in an Android app.

## Setup

1. Create a new Android project
2. Add the library dependency as described in the main README
3. Add Firebase configuration
4. Copy the following code to your project

## Example Code

### 1. Application Class

```java
public class ExampleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Firebase will be initialized automatically
    }
}
```

### 2. Custom Notification Handler

```java
public class ExampleFirebaseMessagingService extends NotifyFirebaseMessagingService {
    @Override
    protected void handleDataMessage(Map<String, String> data) {
        // Parse the notification data
        Gson gson = new Gson();
        String jsonData = gson.toJson(data);
        NotificationPayload payload = gson.fromJson(jsonData, NotificationPayload.class);
        
        // Handle the notification based on type
        if (payload != null && payload.getData() != null) {
            String type = payload.getData().getType();
            switch (type) {
                case "message":
                    // Handle message notification
                    break;
                case "update":
                    // Handle update notification
                    break;
                default:
                    // Handle other types
                    break;
            }
        }
        
        // Call super to show the notification
        super.handleDataMessage(data);
    }
}
```

### 3. AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.app">

    <application
        android:name=".ExampleApplication"
        ...>

        <service
            android:name=".ExampleFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>

    </application>

</manifest>
```

## Testing Notifications

1. Get your FCM token:
```java
FirebaseMessaging.getInstance().getToken()
    .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            String token = task.getResult();
            Log.d("FCM", "Token: " + token);
        }
    });
```

2. Send a test notification using Firebase Console or this curl command:
```bash
curl -X POST \
  https://fcm.googleapis.com/fcm/send \
  -H 'Authorization: key=YOUR_SERVER_KEY' \
  -H 'Content-Type: application/json' \
  -d '{
    "to": "DEVICE_TOKEN",
    "notification": {
      "title": "Test Notification",
      "body": "This is a test notification"
    },
    "data": {
      "type": "message",
      "id": "123",
      "extra": "test data"
    }
  }'
```

## Customization

### Custom Notification Icon

1. Create a new vector drawable in `res/drawable/ic_notification.xml`
2. Make sure it's a white icon
3. The library will use this icon for notifications

### Custom Notification Channel

The library creates a default notification channel. To customize it:

```java
public class ExampleFirebaseMessagingService extends NotifyFirebaseMessagingService {
    @Override
    protected void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Custom Channel Name",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Custom channel description");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
``` 