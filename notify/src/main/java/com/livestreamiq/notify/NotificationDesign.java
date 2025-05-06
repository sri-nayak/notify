package com.livestreamiq.notify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotificationDesign {
    private static final String TAG = "NotificationDesign";
    private static final Executor executor = Executors.newSingleThreadExecutor();
    
    private final Context context;
    private final Gson gson;
    private final NotificationAssetLoader assetLoader;
    
    public interface DesignCallback {
        void onDesignApplied(NotificationCompat.Builder builder);
        void onDesignError(Exception e);
    }
    
    public NotificationDesign(Context context) {
        this.context = context;
        this.gson = new Gson();
        this.assetLoader = new NotificationAssetLoader(context);
    }
    
    public void applyDesign(JSONObject designConfig, DesignCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                String type = designConfig.getString("type");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");
                
                switch (type) {
                    case "template":
                        applyTemplateDesign(designConfig, builder);
                        break;
                    case "custom":
                        applyCustomDesign(designConfig, builder);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown design type: " + type);
                }
                
                callback.onDesignApplied(builder);
            } catch (Exception e) {
                Log.e(TAG, "Error applying design", e);
                callback.onDesignError(e);
            }
        }, executor);
    }
    
    private void applyTemplateDesign(JSONObject designConfig, NotificationCompat.Builder builder) 
            throws JSONException, IOException {
        String templateId = designConfig.getString("template_id");
        JSONObject style = designConfig.getJSONObject("style");
        JSONObject variables = designConfig.getJSONObject("variables");
        
        // Apply style
        if (style.has("background")) {
            String background = style.getString("background");
            if (background.startsWith("#")) {
                builder.setColor(Color.parseColor(background));
            } else if (background.equals("gradient")) {
                // Handle gradient background
                String[] colors = gson.fromJson(style.getJSONArray("colors").toString(), String[].class);
                // Apply gradient
            }
        }
        
        // Apply template-specific styling
        switch (templateId) {
            case "promo_banner":
                applyPromoBannerStyle(builder, variables);
                break;
            case "message_card":
                applyMessageCardStyle(builder, variables);
                break;
            case "alert":
                applyAlertStyle(builder, variables);
                break;
            default:
                throw new IllegalArgumentException("Unknown template: " + templateId);
        }
    }
    
    private void applyCustomDesign(JSONObject designConfig, NotificationCompat.Builder builder) 
            throws JSONException, IOException {
        String layoutUrl = designConfig.getString("layout_url");
        JSONObject assets = designConfig.getJSONObject("assets");
        
        // Download and cache assets
        Map<String, Bitmap> downloadedAssets = assetLoader.loadAssets(assets);
        
        // Create custom RemoteViews
        RemoteViews customView = createCustomRemoteViews(layoutUrl, downloadedAssets);
        builder.setCustomContentView(customView);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
    }
    
    private void applyPromoBannerStyle(NotificationCompat.Builder builder, JSONObject variables) 
            throws JSONException, IOException {
        String title = variables.getString("title");
        String buttonText = variables.getString("button_text");
        String backgroundImage = variables.getString("background_image");
        
        // Load background image
        Bitmap background = assetLoader.loadImage(backgroundImage);
        
        // Create custom layout
        RemoteViews customView = new RemoteViews(context.getPackageName(), 
            R.layout.notification_promo_banner);
        
        customView.setTextViewText(R.id.title, title);
        customView.setTextViewText(R.id.button, buttonText);
        customView.setImageViewBitmap(R.id.background, background);
        
        builder.setCustomContentView(customView);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
    }
    
    private void applyMessageCardStyle(NotificationCompat.Builder builder, JSONObject variables) 
            throws JSONException, IOException {
        String avatar = variables.getString("avatar");
        String content = variables.getString("content");
        String timestamp = variables.getString("timestamp");
        
        // Load avatar image
        Bitmap avatarBitmap = assetLoader.loadImage(avatar);
        
        // Create custom layout
        RemoteViews customView = new RemoteViews(context.getPackageName(), 
            R.layout.notification_message_card);
        
        customView.setImageViewBitmap(R.id.avatar, avatarBitmap);
        customView.setTextViewText(R.id.content, content);
        customView.setTextViewText(R.id.timestamp, timestamp);
        
        builder.setCustomContentView(customView);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
    }
    
    private void applyAlertStyle(NotificationCompat.Builder builder, JSONObject variables) 
            throws JSONException {
        String icon = variables.getString("icon");
        String message = variables.getString("message");
        
        // Create custom layout
        RemoteViews customView = new RemoteViews(context.getPackageName(), 
            R.layout.notification_alert);
        
        customView.setImageViewResource(R.id.icon, getIconResource(icon));
        customView.setTextViewText(R.id.message, message);
        
        builder.setCustomContentView(customView);
        builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
    }
    
    private RemoteViews createCustomRemoteViews(String layoutUrl, Map<String, Bitmap> assets) 
            throws IOException, JSONException {
        // Download and parse layout JSON
        String layoutJson = assetLoader.downloadLayout(layoutUrl);
        JSONObject layout = new JSONObject(layoutJson);
        
        // Create RemoteViews based on layout
        RemoteViews customView = new RemoteViews(context.getPackageName(), 
            R.layout.notification_custom);
        
        // Apply layout elements
        // ... implementation details ...
        
        return customView;
    }
    
    private int getIconResource(String iconName) {
        return context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());
    }

    public void clearCache() {
        assetLoader.clearCache();
    }
} 