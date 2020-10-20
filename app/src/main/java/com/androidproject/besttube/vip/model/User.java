package com.androidproject.besttube.vip.model;

public class User {
    private String userUid;
    private String name;
    private String phone;
    private String image;
    private String about;

    public User() {
    }

    public User(String name, String phone, String image, String about, String userUid) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.about = about;
        this.userUid = userUid;

    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
