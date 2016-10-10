package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 5/9/2016.
 */
public class GenericPostLogin {

    @SerializedName("username")
    public String username;

    @SerializedName("salt")
    public String salt;

    @SerializedName("password")
    public String pass;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public GenericPostLogin(String username, String salt, String pass) {
        this.username = username;
        this.salt = salt;
        this.pass = pass;
    }

    public GenericPostLogin() {
        this.username = "app";
        this.salt = "GOPtwGiU4Llh1Rci";
        this.pass = "T0@n8l8*dsRH6wyQ";
    }
}
