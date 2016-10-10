package com.leontran.themobilevn.activity;

import android.content.Intent;
import android.os.Bundle;
import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.CitiProvinceData;
import com.leontran.themobilevn.model.GenericLoadListCityProvince;
import com.leontran.themobilevn.model.JsonDataReturnListCities;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.JsonDataReturnSimpleObjectDta;
import com.leontran.themobilevn.model.ListCitiProvinceData;
import com.leontran.themobilevn.model.ListSimpleObjectData;
import com.leontran.themobilevn.model.SimpleObjectType;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

import java.util.ArrayList;

public class SplashScreenActivity extends BasicActivity {

    private int keyRequest = 0;

    private DataDownloader mDataDownloader = new DataDownloader(
            new DataDownloader.OnDownloadCompletedListener() {

                @Override
                public void onCompleted(Object key, String result) {
                    RequestData contentKey = (RequestData) key;
                    int type = contentKey.type;
                    Gson gson = new Gson();
                    if (type == RequestDataFactory.ACTION_LOGIN ) {
                        JsonDataReturnLogin gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnLogin.class);
                        } catch (Exception e) {
                            UIUtils.showToast(SplashScreenActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            if (keyRequest == RequestDataFactory.ACTION_CITY_GET){
                                getListCities();
                            } else if ( keyRequest == RequestDataFactory.ACTION_PRICE_RANGE_GET){
                               getListPriceRanges();
                            } else if ( keyRequest == RequestDataFactory.ACTION_BRANDS_GET){
                                getListBrand();
                            } else if ( keyRequest == RequestDataFactory.ACTION_DEVICE_TYPE_GET){
                                getListDeviceType();
                            }
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SplashScreenActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_CITY_GET){
                        JsonDataReturnListCities gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnListCities.class);
                        } catch (Exception e) {
                            UIUtils.showToast(SplashScreenActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            ArrayList<CitiProvinceData> listData = gr.getListCityProvince();
                            ListCitiProvinceData temp = new ListCitiProvinceData(listData);
                            MPreferenceManager.setCitiesProvinceList(MPreferenceManager.getJsonFromObject(temp));
                            keyRequest = RequestDataFactory.ACTION_PRICE_RANGE_GET;
                            postLogin(mDataDownloader, true);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SplashScreenActivity.this,
                                        gr.getMessage());
                            }
                        }

                    } else if (type == RequestDataFactory.ACTION_PRICE_RANGE_GET){
                        JsonDataReturnSimpleObjectDta gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSimpleObjectDta.class);
                        } catch (Exception e) {
                            UIUtils.showToast(SplashScreenActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            ArrayList<SimpleObjectType> listData = gr.getListItems();
                            ListSimpleObjectData temp = new ListSimpleObjectData(listData);
                            MPreferenceManager.setPriceRange(MPreferenceManager.getJsonFromObject(temp));
                            keyRequest = RequestDataFactory.ACTION_BRANDS_GET;
                            postLogin(mDataDownloader, true);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SplashScreenActivity.this,
                                        gr.getMessage());
                            }
                        }

                    }  else if (type == RequestDataFactory.ACTION_BRANDS_GET){
                        JsonDataReturnSimpleObjectDta gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSimpleObjectDta.class);
                        } catch (Exception e) {
                            UIUtils.showToast(SplashScreenActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            ArrayList<SimpleObjectType> listData = gr.getListItems();
                            ListSimpleObjectData temp = new ListSimpleObjectData(listData);
                            MPreferenceManager.setBrand(MPreferenceManager.getJsonFromObject(temp));
                            keyRequest = RequestDataFactory.ACTION_DEVICE_TYPE_GET;
                            postLogin(mDataDownloader, true);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SplashScreenActivity.this,
                                        gr.getMessage());
                            }
                        }

                    }  else if (type == RequestDataFactory.ACTION_DEVICE_TYPE_GET){
                        JsonDataReturnSimpleObjectDta gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSimpleObjectDta.class);
                        } catch (Exception e) {
                            UIUtils.showToast(SplashScreenActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            ArrayList<SimpleObjectType> listData = gr.getListItems();
                            ListSimpleObjectData temp = new ListSimpleObjectData(listData);
                            MPreferenceManager.setDeviceType(MPreferenceManager.getJsonFromObject(temp));
                            Intent intentDetail = new Intent(SplashScreenActivity.this, HomePageActivity.class);
                            startActivity(intentDetail);
                            overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SplashScreenActivity.this,
                                        gr.getMessage());
                            }
                        }

                    }

                }

                @Override
                public String doInBackgroundDebug(Object... params) {
                    // TODO Auto-generated method stub
                    return null;
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_splash_screen);
        initLayout();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        keyRequest = RequestDataFactory.ACTION_CITY_GET;
        postLogin(mDataDownloader, true);
    }

    private void initLayout() {

    }

    private void initAdapter() {

    }

    private void getListCities(){
        GenericLoadListCityProvince dataGet = new GenericLoadListCityProvince();
        dataGet.setToken(MPreferenceManager.getAccessToken());
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_CITIES_STRING, dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_CITY_GET);
            startDataDownLoader(data1, DataDownloader.METHOD_GET);
        }
    }

    private void getListPriceRanges(){
        GenericLoadListCityProvince dataGet = new GenericLoadListCityProvince();
        dataGet.setToken(MPreferenceManager.getAccessToken());
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_PRICE_RANGE_STRING, dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_PRICE_RANGE_GET);
            startDataDownLoader(data1, DataDownloader.METHOD_GET);
        }
    }


    private void getListBrand(){
        GenericLoadListCityProvince dataGet = new GenericLoadListCityProvince();
        dataGet.setToken(MPreferenceManager.getAccessToken());
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_BRANDS_STRING, dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_BRANDS_GET);
            startDataDownLoader(data1, DataDownloader.METHOD_GET);
        }
    }

    private void getListDeviceType(){
        GenericLoadListCityProvince dataGet = new GenericLoadListCityProvince();
        dataGet.setToken(MPreferenceManager.getAccessToken());
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_DEVICE_TYPE_STRING, dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_DEVICE_TYPE_GET);
            startDataDownLoader(data1, DataDownloader.METHOD_GET);
        }
    }


    private void startDataDownLoader(RequestData data, int type) {
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
            mDataDownloader.addRequestJson(data, data.params, type);
        }
    }


}
