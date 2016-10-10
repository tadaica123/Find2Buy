package com.leontran.themobilevn.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.adapter.ListStoreAdapter;
import com.leontran.themobilevn.layouts.ExpandableHeightListView;
import com.leontran.themobilevn.model.DeviceDataDetail;
import com.leontran.themobilevn.model.DeviceMoreDetail;
import com.leontran.themobilevn.model.GenericLoadProductDetail;
import com.leontran.themobilevn.model.GenericPostLogin;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.JsonDataReturnProductDetail;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

import java.util.ArrayList;


public class ListStoreAcitivty extends BasicActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {


    public static int ORDER_ASCENDING = 0;
    public static int ORDER_DECREASE = 1;

    protected static final int REQUEST_CHECK_SETTINGS = 0x3;

    private FloatingActionButton btnFab;
    private boolean isShowSetting = false;
    private int type_order = 0, active_nearest = 0;
    private boolean canGetLocation = false;
    private CheckBox cbNearPriority;
    private RadioButton raAscending, reDecrease;
    private ImageView btnRefresh;
    private ExpandableHeightListView lvStore;
    private SwipeRefreshLayout swipeContainer;
    private DeviceDataDetail dataGet;
    private ArrayList<DeviceMoreDetail> listStore;
    private ListStoreAdapter mAdapter;
    private boolean isRefresh = false;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;

    private DataDownloader mDataDownloader = new DataDownloader(
            new DataDownloader.OnDownloadCompletedListener() {

                @Override
                public void onCompleted(Object key, String result) {
                    RequestData contentKey = (RequestData) key;
                    int type = contentKey.type;
                    Gson gson = new Gson();
                    if (type == RequestDataFactory.ACTION_LOGIN) {
                        JsonDataReturnLogin gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnLogin.class);
                        } catch (Exception e) {
                            UIUtils.showToast(ListStoreAcitivty.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            getListProductDetail();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(ListStoreAcitivty.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_PRODUCT_GET) {
                        JsonDataReturnProductDetail gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnProductDetail.class);
                        } catch (Exception e) {
                            UIUtils.showToast(ListStoreAcitivty.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            if (gr.getData() != null) {
                                dataGet = gr.getData();
                                listStore = new ArrayList<>();
                                if (dataGet.getDetail() != null && dataGet.getDetail().size() > 0) {
                                    listStore.addAll(dataGet.getDetail());
                                }
                                initData();
                                initAdapter();
                                final ScrollView sv = (ScrollView) findViewById(R.id.scrollView);
                                sv.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        sv.fullScroll(ScrollView.FOCUS_UP);
                                    }
                                });
                            }
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(ListStoreAcitivty.this,
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
        setContentView(R.layout.activity_list_device_page);
        initSlidingMenu();
        initLayout();
        postLogin(mDataDownloader, isRefresh);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
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

        btnFab = (FloatingActionButton) findViewById(R.id.fab);
        btnFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowSetting) {
                    isShowSetting = false;
                    initPopupSettingSearch(isShowSetting);
                } else {
                    isShowSetting = true;
                    initPopupSettingSearch(isShowSetting);
                }
            }
        });
        cbNearPriority = (CheckBox) findViewById(R.id.cb_near_priority);
        cbNearPriority.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    active_nearest = 1;
                } else {
                    active_nearest = 0;
                }
            }
        });
        raAscending = (RadioButton) findViewById(R.id.rd_ascending);
        raAscending.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    type_order = ORDER_ASCENDING;
                } else {
                    type_order = ORDER_DECREASE;
                }
            }
        });
        reDecrease = (RadioButton) findViewById(R.id.rd_decrease);


    }

    private void initData() {
        ImageView img = (ImageView) findViewById(R.id.img);
        mImgLoader.displayImage(dataGet.getImage(), img, options);
        TextView txt = (TextView) findViewById(R.id.txt_name);
        txt.setText(dataGet.getName());
        txt = (TextView) findViewById(R.id.txt_price_min);
        txt.setText(dataGet.getMin_price());
        txt = (TextView) findViewById(R.id.txt_price_max);
        txt.setText(dataGet.getMax_price());
    }

    private void initAdapter() {
        lvStore = (ExpandableHeightListView) findViewById(R.id.lv_store);
        lvStore.setExpanded(true);
        mAdapter = new ListStoreAdapter(this, listStore, this);
        lvStore.setAdapter(mAdapter);
    }


    private void initPopupSettingSearch(boolean isShow) {
        LinearLayout layoutSetting = (LinearLayout) findViewById(R.id.layout_setting_search);
        layoutSetting.setVisibility(View.GONE);

        Animation animShow = AnimationUtils.loadAnimation(this, R.anim.slide_up_dlg);
        Animation animHide = AnimationUtils.loadAnimation(this, R.anim.slide_bottom);
        if (isShow) {
            layoutSetting.setVisibility(View.VISIBLE);
            layoutSetting.startAnimation(animShow);
            btnFab.setImageResource(R.drawable.ic_popup_down);
        } else {
            layoutSetting.startAnimation(animHide);
            layoutSetting.setVisibility(View.GONE);
            btnFab.setImageResource(R.drawable.ic_settings);
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {

        // This code apply for android 6.0
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    10);
            return;
        }
        //////////////////////////////////////

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            canGetLocation = true;
        } else {
            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ex) {
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!gps_enabled && !network_enabled) {
                LocationRequest mLocationRequestHighAccuracy = LocationRequest.create();
                mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                mLocationRequestHighAccuracy.setInterval(30 * 1000);
                mLocationRequestHighAccuracy.setFastestInterval(5 * 1000);

                LocationRequest mLocationRequestBalancedPowerAccuracy = LocationRequest.create();
                mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationRequestBalancedPowerAccuracy.setInterval(30 * 1000);
                mLocationRequestHighAccuracy.setFastestInterval(5 * 1000);

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                        .addLocationRequest(mLocationRequestHighAccuracy)
                        .addLocationRequest(mLocationRequestBalancedPowerAccuracy);

                //**************************
                builder.setAlwaysShow(true); //this is the key ingredient
                //**************************
                PendingResult<LocationSettingsResult> result =
                        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {

                        final Status status = result.getStatus();
                        final LocationSettingsStates state = result.getLocationSettingsStates();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:

                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the user
                                // a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    status.startResolutionForResult(
                                            ListStoreAcitivty.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                });
            } else {
                Snackbar.make(btnFab, "Khổng thể lấy được vị trí của bạn hiện tại, một số chức năng sẽ bị hạn chế.", Snackbar.LENGTH_LONG).show();
            }
        }

        if (canGetLocation) {
            cbNearPriority.setEnabled(true);
        } else {
            cbNearPriority.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (ContextCompat.checkSelfPermission(ListStoreAcitivty.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                            mGoogleApiClient);
                                    if (mLastLocation != null) {
                                        canGetLocation = true;
                                    } else {
                                        Snackbar.make(btnFab, "Khổng thể lấy được vị trí của bạn hiện tại, một số chức năng sẽ bị hạn chế.", Snackbar.LENGTH_LONG).show();
                                    }
                                    if (canGetLocation) {
                                        cbNearPriority.setEnabled(true);
                                    } else {
                                        cbNearPriority.setEnabled(false);
                                    }
                                    postLogin(mDataDownloader, isRefresh);
                                }
                            }
                        }, 2000);
                        break;
                    case Activity.RESULT_CANCELED:
                        Snackbar.make(btnFab, "Bạn đã từ chối một số quyền của ứng dụng, một số chức năng sẽ bị hạn chế.", Snackbar.LENGTH_LONG).show();
                        break;
                    default:
                        break;
                }
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                mGoogleApiClient);
                        if (mLastLocation != null) {
                            canGetLocation = true;
                        } else {
                            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                            boolean gps_enabled = false;
                            boolean network_enabled = false;

                            try {
                                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            } catch (Exception ex) {
                            }

                            try {
                                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                            } catch (Exception ex) {
                            }

                            if (!gps_enabled && !network_enabled) {
                                LocationRequest mLocationRequestHighAccuracy = LocationRequest.create();
                                mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                mLocationRequestHighAccuracy.setInterval(30 * 1000);
                                mLocationRequestHighAccuracy.setFastestInterval(5 * 1000);

                                LocationRequest mLocationRequestBalancedPowerAccuracy = LocationRequest.create();
                                mLocationRequestBalancedPowerAccuracy.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                                mLocationRequestBalancedPowerAccuracy.setInterval(30 * 1000);
                                mLocationRequestHighAccuracy.setFastestInterval(5 * 1000);

                                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                        .addLocationRequest(mLocationRequestHighAccuracy)
                                        .addLocationRequest(mLocationRequestBalancedPowerAccuracy);

                                //**************************
                                builder.setAlwaysShow(true); //this is the key ingredient
                                //**************************
                                PendingResult<LocationSettingsResult> result =
                                        LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

                                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                                    @Override
                                    public void onResult(LocationSettingsResult result) {
                                        final Status status = result.getStatus();
                                        final LocationSettingsStates state = result.getLocationSettingsStates();
                                        switch (status.getStatusCode()) {
                                            case LocationSettingsStatusCodes.SUCCESS:

                                                break;
                                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                                // Location settings are not satisfied. But could be fixed by showing the user
                                                // a dialog.
                                                try {
                                                    // Show the dialog by calling startResolutionForResult(),
                                                    // and check the result in onActivityResult().
                                                    status.startResolutionForResult(
                                                            ListStoreAcitivty.this, REQUEST_CHECK_SETTINGS);
                                                } catch (IntentSender.SendIntentException e) {
                                                    // Ignore the error.
                                                }
                                                break;
                                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                                // Location settings are not satisfied. However, we have no way to fix the
                                                // settings so we won't show the dialog.
                                                break;
                                        }
                                    }
                                });
                            }
                        }
                        if (canGetLocation) {
                            cbNearPriority.setEnabled(true);
                        } else {
                            cbNearPriority.setEnabled(false);
                        }
                    }
                } else {
                    Snackbar.make(btnFab, "Bạn đã từ chối một số quyền của ứng dụng, một số chức năng sẽ bị hạn chế.", Snackbar.LENGTH_LONG).show();
                    if (canGetLocation) {
                        cbNearPriority.setEnabled(true);
                    } else {
                        cbNearPriority.setEnabled(false);
                    }
                }
                postLogin(mDataDownloader, false);
                return;
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Snackbar.make(btnFab, "Kết nối hệ thống định vị thất bại", Snackbar.LENGTH_LONG).show();
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
        mGoogleApiClient.connect();
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
        AppIndex.AppIndexApi.start(mGoogleApiClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mDataDownloader != null) {
            mDataDownloader.ExitTask();
        }
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
        AppIndex.AppIndexApi.end(mGoogleApiClient, viewAction);
        mGoogleApiClient.disconnect();
    }


    private void getListProductDetail() {
        GenericLoadProductDetail dataGet = new GenericLoadProductDetail();
        dataGet.setToken(MPreferenceManager.getAccessToken());
        if (active_nearest == 1) {
            dataGet.setCurrent_lat((float) mLastLocation.getLatitude());
            dataGet.setCurrent_lng((float) mLastLocation.getLongitude());
            dataGet.setRadius(10000);
        }
        if (MPreferenceManager.isConnectionAvailable(this)) {
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_PRODUCT_STRING + "/" + MPreferenceManager.getProductId(), dataGet.getStringRequest(),
                    RequestDataFactory.ACTION_PRODUCT_GET);
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
            case R.id.btn_back:
                finish();
                break;
            case R.id.layout_main:
                DeviceMoreDetail data = (DeviceMoreDetail) v.getTag();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getUrl()));
                startActivity(browserIntent);
                break;
            case R.id.layout_device_information:
                data = (DeviceMoreDetail) v.getTag();
                Intent intentDetail = new Intent(ListStoreAcitivty.this, InformationDeivceActivity.class);
                intentDetail.putExtra(InformationDeivceActivity.HTML, data.getInformation());
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                break;
            case R.id.btn_show_in_map:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Bạn đã không cho phép ứng dụng quyền sử dụng chức năng này. Chức năng này sẽ tạm đóng.", Toast.LENGTH_LONG).show();
                    break;
                }
//                data = (DeviceMoreDetail) v.getTag();
                intentDetail = new Intent(ListStoreAcitivty.this, ShowMapActivity.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
                break;
            default:
                break;
        }
    }
}
