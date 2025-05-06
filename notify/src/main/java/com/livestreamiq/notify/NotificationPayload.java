package com.livestreamiq.notify;

public class NotificationPayload {
    private String title;
    private String body;
    private String imageUrl;
    private NotificationData data;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public NotificationData getData() {
        return data;
    }

    public void setData(NotificationData data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NotificationPayload{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", data=" + data +
                '}';
    }
} 