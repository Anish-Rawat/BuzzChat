package com.example.buzzchat;

public class UserInfoDataModel {

    String userProfile;
    String userId;
    String name;
    String nickname;
    String about;
    String dob;
    String gender;
    String country;
    String contactNumber;
    String address;
    String state;

    String onlineTiming;
    String onlineDate;
    public UserInfoDataModel() {
    }
    public UserInfoDataModel(String userprofile, String userId, String name, String nickname, String about, String dob, String gender, String country, String contactNumber, String address) {
        this.userProfile = userprofile;
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.about = about;
        this.dob = dob;
        this.gender = gender;
        this.country = country;
        this.contactNumber = contactNumber;
        this.address = address;
    }
    public UserInfoDataModel(String userId, String name, String nickname, String about, String dob, String gender, String country, String contactNumber, String address) {
        this.userId = userId;
        this.name = name;
        this.nickname = nickname;
        this.about = about;
        this.dob = dob;
        this.gender = gender;
        this.country = country;
        this.contactNumber = contactNumber;
        this.address = address;
    }

    public UserInfoDataModel(Object value) {

    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userprofile) {
        this.userProfile = userprofile;
    }
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getOnlineTiming() {
        return onlineTiming;
    }

    public void setOnlineTiming(String onlineTiming) {
        this.onlineTiming = onlineTiming;
    }

    public String getOnlineDate() {
        return onlineDate;
    }

    public void setOnlineDate(String onlineDate) {
        this.onlineDate = onlineDate;
    }

}

