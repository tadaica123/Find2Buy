package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 5/9/2016.
 */
public class JsonDataReturnListProduct {

    @SerializedName("status")
    public int status;

    @SerializedName("msg")
    public String message;

    @SerializedName("has_next")
    public boolean has_next;

    @SerializedName("items")
    public ArrayList<DeviceData> items;

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

    public boolean isHas_next() {
        return has_next;
    }

    public void setHas_next(boolean has_next) {
        this.has_next = has_next;
    }

    public ArrayList<DeviceData> getItems() {
        return items;
    }

    public void setItems(ArrayList<DeviceData> items) {
        this.items = items;
    }
}
