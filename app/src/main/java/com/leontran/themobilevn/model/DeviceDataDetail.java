package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 5/9/2016.
 */
public class DeviceDataDetail {

    @SerializedName("name")
    public String name;

    @SerializedName("image")
    public String image;

    @SerializedName("min_price")
    public String min_price;

    @SerializedName("max_price")
    public String max_price;

    @SerializedName("store_count")
    public int store_count;

    @SerializedName("detail")
    public ArrayList<DeviceMoreDetail> detail;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public int getStore_count() {
        return store_count;
    }

    public void setStore_count(int store_count) {
        this.store_count = store_count;
    }

    public ArrayList<DeviceMoreDetail> getDetail() {
        return detail;
    }

    public void setDetail(ArrayList<DeviceMoreDetail> detail) {
        this.detail = detail;
    }
}
