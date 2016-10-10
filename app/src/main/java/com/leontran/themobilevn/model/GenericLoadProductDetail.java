package com.leontran.themobilevn.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 5/9/2016.
 */
public class GenericLoadProductDetail {

    @SerializedName("token")
    public String token;

    @SerializedName("current_lat")
    public float current_lat;

    @SerializedName("current_lng")
    public float current_lng;

    @SerializedName("radius")
    public int radius;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public float getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(float current_lat) {
        this.current_lat = current_lat;
    }

    public float getCurrent_lng() {
        return current_lng;
    }

    public void setCurrent_lng(float current_lng) {
        this.current_lng = current_lng;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getStringRequest(){
        String uri = Uri.parse("")
                .buildUpon()
                .appendQueryParameter("token", token)
                .appendQueryParameter("current_lat", current_lat + "")
                .appendQueryParameter("current_lng", current_lng + "")
                .appendQueryParameter("radius", radius + "")
                .build().toString();
        return uri;
    }
}
