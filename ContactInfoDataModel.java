package com.example.buzzchat;

public class ContactInfoDataModel {

    String userProfile,name,nickname,about,userId;

    public ContactInfoDataModel() {

    }
    public ContactInfoDataModel(String userProfile, String userName, String userNickname, String userAbout, String userId) {
        this.userProfile = userProfile;
        this.name = userName;
        this.nickname = userNickname;
        this.about = userAbout;
        this.userId = userId;
    }

    public ContactInfoDataModel(String userName, String userNickname, String userAbout,String userId) {
        this.userId = userId;
        this.name = userName;
        this.nickname = userNickname;
        this.about = userAbout;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

