package com.leontran.themobilevn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.adapter.ListDeviceAdapter;
import com.leontran.themobilevn.model.DeviceData;
import com.leontran.themobilevn.model.GenericLoadListProduct;
import com.leontran.themobilevn.model.GenericLoadListProductCustomize;
import com.leontran.themobilevn.model.JsonDataReturnListProduct;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 7/1/2016.
 */
public class CustomizeSearchResultActivity extends BasicActivity implements View.OnClickListener {

    private SwipeRefreshLayout swipeContainer;
    private ListView lvData;
    private TextView txtNoData;
    private ImageView btnRefresh;
    private ArrayList<DeviceData> listDeivceResult;
    private ListDeviceAdapter mAdapter;
    private boolean isRefresh = false;

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
                            UIUtils.showToast(CustomizeSearchResultActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            getListProduct(gr.getToken());
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(CustomizeSearchResultActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_ADVANCE_SEARCH_GET){
                        JsonDataReturnListProduct gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnListProduct.class);
                        } catch (Exception e) {
                            UIUtils.showToast(CustomizeSearchResultActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            listDeivceResult = new ArrayList<>();

                            if (gr.getItems() != null  && gr.getItems().size() > 0){
                                listDeivceResult.addAll(gr.getItems());
                            }
                            if (listDeivceResult.size() == 0){
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText(String.format(getResources().getString(R.string.no_data_advance)));
                            } else {
                                txtNoData.setVisibility(View.GONE);
                            }
                            initAdapter();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(CustomizeSearchResultActivity.this,
                                        gr.getMessage());
                            }
                        }
                        hideDialogLoading();
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
        setContentView(R.layout.activity_customize_search_result_page);
        initSlidingMenu();
        initLayout();
        setNavigationItemHightLight(0, 1);
    }

    private void initLayout() {
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        btnRefresh = (ImageView) toolbar.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                postLogin(mDataDownloader, isRefresh);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvData = (ListView) findViewById(R.id.list_product);
        txtNoData  = (TextView) findViewById(R.id.txt_no_data);
    }

    private void initAdapter(){
        mAdapter = new ListDeviceAdapter(this, listDeivceResult, this);
        lvData.setAdapter(mAdapter);
    }

    private void getListProduct(String accessToken){
        GenericLoadListProductCustomize dataGet = MPreferenceManager.getCustomizeSearch();
        dataGet.setToken(accessToken);
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_ADVANCE_SEARCH_GET_STRING, dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_ADVANCE_SEARCH_GET);
            startDataDownLoader(data1, DataDownloader.METHOD_GET);
        }
    }

    private void startDataDownLoader(RequestData data, int type) {
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
            mDataDownloader.addRequestJson(data, data.params, type);
            if (!isRefresh){
                showDialogLoading();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        postLogin(mDataDownloader, isRefresh);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_main:
                DeviceData data = (DeviceData) v.getTag();
                MPreferenceManager.setProductId(data.getId());
                Intent intentDetail = new Intent(CustomizeSearchResultActivity.this, ListStoreAcitivty.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
}