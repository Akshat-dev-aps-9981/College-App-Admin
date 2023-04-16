package com.aksapps.collageappadmin.Models;

import androidx.annotation.Keep;

@Keep
public class TeacherData {
    private String name = "", email = "", phone = "", image = "", uniqueKey = "";

    public TeacherData() {}

    public TeacherData(String name, String email, String phone, String image, String uniqueKey) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.image = image;
        this.uniqueKey = uniqueKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}
