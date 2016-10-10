package com.leontran.themobilevn.model;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 7/25/2016.
 */
public class GenericLoadListCityProvince {

    @SerializedName("token")
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStringRequest(){
        String uri = Uri.parse("")
                .buildUpon()
                .appendQueryParameter("token", token)
                .build().toString();
        return uri;
    }
}
