package com.aksapps.collageappadmin.Models;

import androidx.annotation.Keep;

@Keep
public class PushNotification {
    private com.aksapps.collageappadmin.Models.NotificationData data;
    private String to;

    public PushNotification() {
    }

    public PushNotification(com.aksapps.collageappadmin.Models.NotificationData data, String to) {
        this.data = data;
        this.to = to;
    }

    public com.aksapps.collageappadmin.Models.NotificationData getData() {
        return data;
    }

    public void setData(com.aksapps.collageappadmin.Models.NotificationData data) {
        this.data = data;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
