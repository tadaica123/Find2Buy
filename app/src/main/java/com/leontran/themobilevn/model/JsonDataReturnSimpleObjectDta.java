package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 7/25/2016.
 */
public class JsonDataReturnSimpleObjectDta {

    @SerializedName("status")
    public int status;

    @SerializedName("msg")
    public String message;

    @SerializedName("items")
    public ArrayList<SimpleObjectType> listItems;

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

    public ArrayList<SimpleObjectType> getListItems() {
        return listItems;
    }

    public void setListItems(ArrayList<SimpleObjectType> listItems) {
        this.listItems = listItems;
    }
}
