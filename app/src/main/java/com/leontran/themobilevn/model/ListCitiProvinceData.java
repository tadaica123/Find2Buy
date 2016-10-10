package com.leontran.themobilevn.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 7/28/2016.
 */
public class ListCitiProvinceData  {


    private ArrayList<CitiProvinceData> arrayData;

    public ListCitiProvinceData(ArrayList<CitiProvinceData> arrayData) {
        this.arrayData = arrayData;
    }

    public ArrayList<CitiProvinceData> getArrayData() {
        return arrayData;
    }

    public void setArrayData(ArrayList<CitiProvinceData> arrayData) {
        this.arrayData = arrayData;
    }

}
