package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by NguyenTa.Tran on 7/25/2016.
 */
public class SimpleObjectType {


    public SimpleObjectType(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
