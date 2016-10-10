package com.leontran.themobilevn.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 7/28/2016.
 */
public class GenericLoadListProductCustomize {

    @SerializedName("token")
    public String token;

    @SerializedName("page")
    public int page;

    @SerializedName("limit")
    public int limit;

    @SerializedName("keyword")
    public String keyword;

    @SerializedName("city_id")
    public String city_id;

    @SerializedName("district_id")
    public String district_id;

    @SerializedName("price_range_id")
    public String price_range_id;

    @SerializedName("device_type_id")
    public String device_type_id;

    @SerializedName("brand_id")
    public String brand_id;

    @SerializedName("current_lat")
    public String current_lat;

    @SerializedName("current_lng")
    public String current_lng;

    @SerializedName("radius")
    public String radius;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }


    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getDistrict_id() {
        return district_id;
    }

    public void setDistrict_id(String district_id) {
        this.district_id = district_id;
    }

    public String getPrice_range_id() {
        return price_range_id;
    }

    public void setPrice_range_id(String price_range_id) {
        this.price_range_id = price_range_id;
    }

    public String getDevice_type_id() {
        return device_type_id;
    }

    public void setDevice_type_id(String device_type_id) {
        this.device_type_id = device_type_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getCurrent_lat() {
        return current_lat;
    }

    public void setCurrent_lat(String current_lat) {
        this.current_lat = current_lat;
    }

    public String getCurrent_lng() {
        return current_lng;
    }

    public void setCurrent_lng(String current_lng) {
        this.current_lng = current_lng;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getStringRequest(){
        String uri = Uri.parse("")
                .buildUpon()
                .appendQueryParameter("token", token)
                .appendQueryParameter("page", page + "")
                .appendQueryParameter("limit", limit + "")
                .appendQueryParameter("keyword", keyword)
                .appendQueryParameter("city_id", city_id)
                .appendQueryParameter("district_id", district_id)
                .appendQueryParameter("price_range_id", price_range_id)
                .appendQueryParameter("device_type_id", device_type_id)
                .appendQueryParameter("brand_id", brand_id)
                .appendQueryParameter("current_lat", current_lat)
                .appendQueryParameter("current_lng", current_lng)
                .appendQueryParameter("radius", radius)
                .build().toString();
        return uri;
    }

}
