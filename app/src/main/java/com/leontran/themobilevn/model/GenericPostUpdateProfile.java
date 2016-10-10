package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 8/21/2016.
 */
public class GenericPostUpdateProfile {

    @SerializedName("email")
    public String email;

    @SerializedName("user_access_token")
    public String user_access_token;

    @SerializedName("name")
    public String name;

    @SerializedName("address")
    public String address;

    @SerializedName("phone_number")
    public String phone_number;

    @SerializedName("token")
    public String token;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
