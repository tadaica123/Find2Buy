package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 8/16/2016.
 */
public class UserProfileData {
    public String user_access_token;

    public String name;

    public String email;

    public String phone;

    public String address;

    public String getUser_access_token() {
        return user_access_token;
    }

    public void setUser_access_token(String user_access_token) {
        this.user_access_token = user_access_token;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
