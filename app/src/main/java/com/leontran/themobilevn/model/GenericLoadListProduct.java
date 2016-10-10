package com.leontran.themobilevn.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 5/9/2016.
 */
public class GenericLoadListProduct {

    @SerializedName("token")
    public String token;

    @SerializedName("page")
    public int page;

    @SerializedName("limit")
    public int limit;

    @SerializedName("keyword")
    public String keyword;

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

    public String getStringRequest(){
        String uri = Uri.parse("")
                .buildUpon()
                .appendQueryParameter("token", token)
                .appendQueryParameter("page", page + "")
                .appendQueryParameter("limit", limit + "")
                .appendQueryParameter("keyword", keyword)
                .build().toString();
        return uri;
    }
}
