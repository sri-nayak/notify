package com.livestreamiq.notify;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

public class Notify {
    private static final String TAG = "Notify";
    private static boolean initialized = false;

    public static void initialize(Context context) {
        if (initialized) {
            return;
        }

        try {
            // Initialize Firebase if not already initialized
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context);
            }

            // Get FCM token
            FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        Log.d(TAG, "FCM Token: " + token);
                    } else {
                        Log.e(TAG, "Failed to get FCM token", task.getException());
                    }
                });

            initialized = true;
            Log.d(TAG, "Notify library initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize Notify library", e);
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }
} 