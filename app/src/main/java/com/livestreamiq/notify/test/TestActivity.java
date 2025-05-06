package com.livestreamiq.notify.test;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.livestreamiq.notify.NotifyManager;

import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {
    private static final String TAG = "NotifyTest";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    
    private TextView tokenTextView;
    private Button testDefaultBtn;
    private Button testTemplateBtn;
    private Button testCustomBtn;
    private Button subscribeBtn;
    private Button unsubscribeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize views
        tokenTextView = findViewById(R.id.tokenTextView);
        testDefaultBtn = findViewById(R.id.testDefaultBtn);
        testTemplateBtn = findViewById(R.id.testTemplateBtn);
        testCustomBtn = findViewById(R.id.testCustomBtn);
        subscribeBtn = findViewById(R.id.subscribeBtn);
        unsubscribeBtn = findViewById(R.id.unsubscribeBtn);

        // Request notification permission
        requestNotificationPermission();

        // Get FCM token
        getFCMToken();

        // Set up test buttons
        setupTestButtons();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, 
                    Manifest.permission.POST_NOTIFICATIONS) != 
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    private void getFCMToken() {
        NotifyManager.getInstance(this).getToken(new NotifyManager.TokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                runOnUiThread(() -> {
                    tokenTextView.setText("FCM Token: " + token);
                    Log.d(TAG, "FCM Token: " + token);
                });
            }

            @Override
            public void onTokenError(Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(TestActivity.this, 
                        "Error getting token: " + e.getMessage(), 
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setupTestButtons() {
        // Test default notification
        testDefaultBtn.setOnClickListener(v -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "test");
            data.put("id", "1");
            
            RemoteMessage message = createTestMessage(
                "Default Notification",
                "This is a test notification",
                data
            );
            
            NotifyManager.getInstance(this).handleMessage(message);
        });

        // Test template notification
        testTemplateBtn.setOnClickListener(v -> {
            Map<String, String> data = new HashMap<>();
            data.put("design", createTemplateDesign());
            
            RemoteMessage message = createTestMessage(
                "Template Notification",
                "This is a template notification",
                data
            );
            
            NotifyManager.getInstance(this).handleMessage(message);
        });

        // Test custom notification
        testCustomBtn.setOnClickListener(v -> {
            Map<String, String> data = new HashMap<>();
            data.put("design", createCustomDesign());
            
            RemoteMessage message = createTestMessage(
                "Custom Notification",
                "This is a custom notification",
                data
            );
            
            NotifyManager.getInstance(this).handleMessage(message);
        });

        // Subscribe to topic
        subscribeBtn.setOnClickListener(v -> {
            NotifyManager.getInstance(this)
                .subscribeToTopic("test_topic")
                .thenAccept(aVoid -> {
                    runOnUiThread(() -> {
                        Toast.makeText(TestActivity.this, 
                            "Subscribed to test_topic", 
                            Toast.LENGTH_SHORT).show();
                    });
                })
                .exceptionally(throwable -> {
                    runOnUiThread(() -> {
                        Toast.makeText(TestActivity.this, 
                            "Subscription failed: " + throwable.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
                    return null;
                });
        });

        // Unsubscribe from topic
        unsubscribeBtn.setOnClickListener(v -> {
            NotifyManager.getInstance(this)
                .unsubscribeFromTopic("test_topic")
                .thenAccept(aVoid -> {
                    runOnUiThread(() -> {
                        Toast.makeText(TestActivity.this, 
                            "Unsubscribed from test_topic", 
                            Toast.LENGTH_SHORT).show();
                    });
                })
                .exceptionally(throwable -> {
                    runOnUiThread(() -> {
                        Toast.makeText(TestActivity.this, 
                            "Unsubscription failed: " + throwable.getMessage(), 
                            Toast.LENGTH_LONG).show();
                    });
                    return null;
                });
        });
    }

    private RemoteMessage createTestMessage(String title, String body, Map<String, String> data) {
        return new RemoteMessage.Builder("test_sender")
            .setMessageId("test_message_id")
            .addData(data)
            .setNotification(new RemoteMessage.Notification(title, body))
            .build();
    }

    private String createTemplateDesign() {
        return "{" +
            "\"type\": \"template\"," +
            "\"template_id\": \"promo_banner\"," +
            "\"style\": {" +
            "    \"background\": \"#2196F3\"" +
            "}," +
            "\"variables\": {" +
            "    \"title\": \"Test Promo\"," +
            "    \"button_text\": \"Click Me\"," +
            "    \"background_image\": \"https://example.com/test.jpg\"" +
            "}" +
            "}";
    }

    private String createCustomDesign() {
        return "{" +
            "\"type\": \"custom\"," +
            "\"layout_url\": \"https://example.com/layouts/test.json\"," +
            "\"assets\": {" +
            "    \"images\": {" +
            "        \"header\": \"https://example.com/test.jpg\"" +
            "    }," +
            "    \"icons\": {" +
            "        \"action\": \"https://example.com/icon.png\"" +
            "    }" +
            "}" +
            "}";
    }
} 