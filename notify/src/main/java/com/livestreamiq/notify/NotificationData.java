package com.livestreamiq.notify;

public class NotificationData {
    private String type;
    private String id;
    private String extra;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "NotificationData{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
} 