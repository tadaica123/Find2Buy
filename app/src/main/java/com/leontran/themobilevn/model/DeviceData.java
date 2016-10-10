package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 4/27/2016.
 */
public class DeviceData {

    @SerializedName("id")
    public String id;

    @SerializedName("image")
    public String image;

    @SerializedName("name")
    public String name;

    @SerializedName("price")
    public String price;

    @SerializedName("store_count")
    public int store_count;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getStore_count() {
        return store_count;
    }

    public void setStore_count(int store_count) {
        this.store_count = store_count;
    }

    public DeviceData(String id, String name, String price, int store_count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.store_count = store_count;
    }
}
