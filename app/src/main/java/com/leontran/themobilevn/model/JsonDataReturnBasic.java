package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 8/16/2016.
 */
public class JsonDataReturnBasic {


    @SerializedName("status")
    public int status;

    @SerializedName("msg")
    public String message;

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
}
