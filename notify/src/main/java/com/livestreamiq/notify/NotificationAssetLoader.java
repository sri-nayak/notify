package com.livestreamiq.notify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NotificationAssetLoader {
    private static final String TAG = "NotificationAssetLoader";
    private static final Executor executor = Executors.newFixedThreadPool(3);
    private static final int CACHE_SIZE = 10 * 1024 * 1024; // 10MB
    
    private final Context context;
    private final LruCache<String, Bitmap> memoryCache;
    private final File cacheDir;
    
    public NotificationAssetLoader(Context context) {
        this.context = context;
        this.memoryCache = new LruCache<String, Bitmap>(CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        this.cacheDir = new File(context.getCacheDir(), "notification_assets");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }
    
    public Map<String, Bitmap> loadAssets(JSONObject assets) throws IOException, JSONException {
        Map<String, Bitmap> result = new HashMap<>();
        
        // Load images
        if (assets.has("images")) {
            JSONObject images = assets.getJSONObject("images");
            Iterator<String> keys = images.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String url = images.getString(key);
                result.put(key, loadImage(url));
            }
        }
        
        // Load icons
        if (assets.has("icons")) {
            JSONObject icons = assets.getJSONObject("icons");
            Iterator<String> keys = icons.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String url = icons.getString(key);
                result.put(key, loadImage(url));
            }
        }
        
        return result;
    }
    
    public Bitmap loadImage(String url) throws IOException {
        // Check memory cache
        Bitmap cached = memoryCache.get(url);
        if (cached != null) {
            return cached;
        }
        
        // Check disk cache
        File cachedFile = new File(cacheDir, getCacheKey(url));
        if (cachedFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(cachedFile.getAbsolutePath());
            if (bitmap != null) {
                memoryCache.put(url, bitmap);
                return bitmap;
            }
        }
        
        // Download and cache
        Bitmap bitmap = downloadImage(url);
        if (bitmap != null) {
            memoryCache.put(url, bitmap);
            cacheToDisk(url, bitmap);
        }
        
        return bitmap;
    }
    
    public String downloadLayout(String url) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            InputStream inputStream = connection.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;
            
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                response.append(new String(buffer, 0, bytesRead));
            }
            
            return response.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private Bitmap downloadImage(String url) throws IOException {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private void cacheToDisk(String url, Bitmap bitmap) {
        CompletableFuture.runAsync(() -> {
            try {
                File cacheFile = new File(cacheDir, getCacheKey(url));
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error caching image", e);
            }
        }, executor);
    }
    
    private String getCacheKey(String url) {
        return String.valueOf(url.hashCode());
    }
    
    public void clearCache() {
        memoryCache.evictAll();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
} 