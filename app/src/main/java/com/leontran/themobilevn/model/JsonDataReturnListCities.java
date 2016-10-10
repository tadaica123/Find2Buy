package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 7/25/2016.
 */
public class JsonDataReturnListCities {

    @SerializedName("status")
    public int status;

    @SerializedName("msg")
    public String message;

    @SerializedName("items")
    public ArrayList<CitiProvinceData> listCityProvince;

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

    public ArrayList<CitiProvinceData> getListCityProvince() {
        return listCityProvince;
    }

    public void setListCityProvince(ArrayList<CitiProvinceData> listCityProvince) {
        this.listCityProvince = listCityProvince;
    }
}
