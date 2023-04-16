package com.aksapps.collageappadmin.Models;

import androidx.annotation.Keep;

@Keep
public class NoticeData {
    private String author = "", title = "", image = "", date = "", time = "", key = "";

    public NoticeData() {}

    public NoticeData(String title, String image, String date, String time, String key) {
        this.title = title;
        this.image = image;
        this.date = date;
        this.time = time;
        this.key = key;
    }

    public NoticeData(String author, String title, String image, String date, String time, String key) {
        this.author = author;
        this.title = title;
        this.image = image;
        this.date = date;
        this.time = time;
        this.key = key;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
