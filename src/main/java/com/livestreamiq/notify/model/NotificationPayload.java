package com.livestreamiq.notify.model;

import com.google.gson.annotations.SerializedName;

public class NotificationPayload {
    @SerializedName("title")
    private String title;
    
    @SerializedName("body")
    private String body;
    
    @SerializedName("template_id")
    private String templateId;
    
    @SerializedName("data")
    private NotificationData data;
    
    @SerializedName("image_url")
    private String imageUrl;
    
    @SerializedName("action_url")
    private String actionUrl;
    
    // Getters and Setters
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getBody() {
        return body;
    }
    
    public void setBody(String body) {
        this.body = body;
    }
    
    public String getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
    
    public NotificationData getData() {
        return data;
    }
    
    public void setData(NotificationData data) {
        this.data = data;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getActionUrl() {
        return actionUrl;
    }
    
    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }
} 