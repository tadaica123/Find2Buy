package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 7/25/2016.
 */
public class CitiProvinceData {

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("districts")
    public ArrayList<SimpleObjectType> listDistrict;

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

    public ArrayList<SimpleObjectType> getListDistrict() {
        return listDistrict;
    }

    public void setListDistrict(ArrayList<SimpleObjectType> listDistrict) {
        this.listDistrict = listDistrict;
    }
}
