package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 8/16/2016.
 */
public class JsonDataReturnSignIn {

    @SerializedName("status")
    public int status;

    @SerializedName("msg")
    public String message;

    @SerializedName("user_access_token")
    public String user_access_token;

    @SerializedName("name")
    public String name;


    public boolean isSuccess(){
        boolean isSuccess = false;
        if (status == 1){
            isSuccess = true;
        }
        return isSuccess;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

}
