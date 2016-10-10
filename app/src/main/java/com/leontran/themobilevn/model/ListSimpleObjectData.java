package com.leontran.themobilevn.model;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 7/28/2016.
 */
public class ListSimpleObjectData {

    private ArrayList<SimpleObjectType> arrayData;

    public ListSimpleObjectData() {

    }

    public ListSimpleObjectData(ArrayList<SimpleObjectType> arrayData) {
        this.arrayData = arrayData;
    }

    public ArrayList<SimpleObjectType> getArrayData() {
        return arrayData;
    }

    public void setArrayData(ArrayList<SimpleObjectType> arrayData) {
        this.arrayData = arrayData;
    }
}
