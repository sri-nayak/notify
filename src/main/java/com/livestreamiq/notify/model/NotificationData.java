package com.livestreamiq.notify.model;

import com.google.gson.annotations.SerializedName;

public class NotificationData {
    @SerializedName("type")
    private String type;
    
    @SerializedName("content")
    private String content;
    
    @SerializedName("metadata")
    private Object metadata;
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Object getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }
} 