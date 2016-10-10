package com.leontran.themobilevn.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.adapter.ListDeviceAdapter;
import com.leontran.themobilevn.model.DeviceData;
import com.leontran.themobilevn.model.GenericLoadListProduct;
import com.leontran.themobilevn.model.GenericPostLogin;
import com.leontran.themobilevn.model.JsonDataReturnListProduct;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

import java.util.ArrayList;


public class HomePageActivity extends BasicActivity
        implements  View.OnClickListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x3;
    private ArrayList<String> listPermission = new ArrayList<>();
    private SwipeRefreshLayout swipeContainer;
    private ListView lvData;
    private ImageView btnRefresh;
    private SearchView searchView;
    private TextView txtNoData;
    private ArrayList<DeviceData> listDeivceResult;
    private ListDeviceAdapter mAdapter;
    private String keyWord = "";
    private boolean isRefresh = false;

    public HomePageActivity() {
    }

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
                            UIUtils.showToast(HomePageActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            getListProduct(keyWord);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(HomePageActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_PRODUCT_GET){
                        JsonDataReturnListProduct gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnListProduct.class);
                        } catch (Exception e) {
                            UIUtils.showToast(HomePageActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            listDeivceResult = new ArrayList<>();

                            if (gr.getItems() != null  && gr.getItems().size() > 0){
                                listDeivceResult.addAll(gr.getItems());
                            }
                            if (listDeivceResult.size() == 0){
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText(String.format(getResources().getString(R.string.no_data), keyWord));
                            } else {
                                txtNoData.setVisibility(View.GONE);
                            }
                            initAdapter();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(HomePageActivity.this,
                                        gr.getMessage());
                            }
                        }
                        swipeContainer.setRefreshing(false);
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
        setContentView(R.layout.activity_home_page);
        initSlidingMenu();
        initLayout();
        initAdapter();
        checkPermission();
        setNavigationItemHightLight(0, 0);
    }

    private void initLayout() {

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

        searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.equals("")){
                    isRefresh = false;
                    keyWord = query;
                    postLogin(mDataDownloader, isRefresh);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        TextView txtNoticeSearch = (TextView) findViewById(R.id.txt_notice);
        txtNoticeSearch.setOnClickListener(this);
        txtNoData  = (TextView) findViewById(R.id.txt_no_data);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView txtNoticeSearch = (TextView) findViewById(R.id.txt_notice);
                txtNoticeSearch.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                TextView txtNoticeSearch = (TextView) findViewById(R.id.txt_notice);
                txtNoticeSearch.setVisibility(View.VISIBLE);

                return false;
            }
        });
        searchView.setSubmitButtonEnabled(true);
        changeSearchViewTextColor(searchView);
    }

    private void initAdapter(){
        mAdapter = new ListDeviceAdapter(this, listDeivceResult, this);
        lvData.setAdapter(mAdapter);
    }

    private void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(getResources().getColor(R.color.gray));
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }

    private void getListProduct(String keyWords){
        GenericLoadListProduct dataGet = new GenericLoadListProduct();
        dataGet.setKeyword(keyWords);
        dataGet.setToken(MPreferenceManager.getAccessToken());
        dataGet.setPage(0);
        dataGet.setLimit(100);
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_PRODUCTS_STRING, dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_PRODUCT_GET);
            startDataDownLoader(data1, DataDownloader.METHOD_GET);
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
    public void onStart() {
        super.onStart();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HomePage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.leontran.themobilevn/http/host/path")
        );
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HomePage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.leontran.themobilevn/http/host/path")
        );

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
    public void onPause() {
        // TODO Auto-generated method stub
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:
                break;
            case R.id.layout_main:
                DeviceData data = (DeviceData) v.getTag();
                MPreferenceManager.setProductId(data.getId());
                Intent intentDetail = new Intent(HomePageActivity.this, ListStoreAcitivty.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.txt_notice:
                searchView.setIconified(false);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

    private void checkPermission(){
        callRequestPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
        callRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        callRequestPermission(Manifest.permission.INTERNET);
        String[] stockArr = new String[listPermission.size()];
        stockArr = listPermission.toArray(stockArr);
        if (listPermission.size() > 0){
            ActivityCompat.requestPermissions(this,
                    stockArr,
                    15);
        }

    }

    private void callRequestPermission(String permission){
        if (ContextCompat.checkSelfPermission(this,
                permission)
                != PackageManager.PERMISSION_GRANTED) {
            listPermission.add(permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull
    String permissions[],@NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode,
                permissions,  grantResults);
        switch (requestCode) {
            case 15: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    break;
                } else {
                    finish();
                }
            }
        }
    }
}
