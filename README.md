# Notify Library

A powerful and easy-to-use notification library for Android that simplifies Firebase Cloud Messaging integration.

## Features

- 🔔 Automatic Firebase Cloud Messaging integration
- 📱 Support for Android 7.0 (API 24) and higher
- 🎯 Customizable notification handling
- 🔒 Secure token management
- 📦 Lightweight and efficient
- 🎨 Material Design support
- 🔄 Background message handling

## Installation

### Step 1: Add the Repository

Add the following to your project's `settings.gradle`:

```groovy
dependencyResolutionManagement {
    repositories {
        // ... other repositories
        maven {
            url "https://raw.githubusercontent.com/sri-nayak/notify/main/repo"
        }
    }
}
```

### Step 2: Add the Dependency

Add the dependency to your app's `build.gradle`:

```groovy
dependencies {
    implementation 'com.livestreamiq:notify:1.0.0'
}
```

### Step 3: Firebase Setup

1. Create a project in the [Firebase Console](https://console.firebase.google.com/)
2. Add your Android app to the project
3. Download the `google-services.json` file
4. Place the `google-services.json` file in your app's module directory

### Step 4: Add Firebase Dependencies

Add the following to your app's `build.gradle`:

```groovy
plugins {
    id 'com.google.gms.google-services'
}

dependencies {
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
}
```

### Step 5: Add Required Permissions

Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## Usage

### Basic Integration

1. Initialize the library in your Application class:

```kotlin
class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Notify.initialize(this)
    }
}
```

2. Register your Application class in `AndroidManifest.xml`:

```xml
<application
    android:name=".YourApplication"
    ...>
    <!-- other configurations -->
</application>
```

### Notification Permission (Android 13+)

For Android 13 (API level 33) and higher, request notification permission in your activity:

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != 
            PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }
}
```

### Sending Notifications

Send notifications using the following JSON payload format:

```json
{
    "notification": {
        "title": "Notification Title",
        "body": "Notification Body"
    },
    "data": {
        "type": "notification_type",
        "content": "notification_content",
        "metadata": {
            "key": "value"
        }
    }
}
```

### Customizing Notification Behavior

You can customize notification behavior by implementing the `NotificationHandler` interface:

```kotlin
class CustomNotificationHandler : NotificationHandler {
    override fun onNotificationReceived(notification: Notification) {
        // Custom handling logic
    }

    override fun onNotificationClicked(notification: Notification) {
        // Custom click handling
    }
}
```

Register your custom handler:

```kotlin
Notify.setNotificationHandler(CustomNotificationHandler())
```

## Requirements

- Android 7.0 (API level 24) or higher
- AndroidX dependencies
- Firebase project setup
- Internet permission
- Notification permission (for Android 13+)

## Proguard Rules

Add the following rules to your `proguard-rules.pro` file:

```proguard
-keep class com.livestreamiq.notify.** { *; }
-keep class com.google.firebase.messaging.** { *; }
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This library is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you encounter any issues or have questions, please:
1. Check the [documentation](https://github.com/sri-nayak/notify/wiki)
2. Search for [existing issues](https://github.com/sri-nayak/notify/issues)
3. Create a new issue if needed

## Credits

Developed by [Sri Nayak](https://github.com/sri-nayak)