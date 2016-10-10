package com.leontran.themobilevn.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;

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
import com.leontran.themobilevn.adapter.CustomSpinnerAdapter;
import com.leontran.themobilevn.model.GenericLoadListProductCustomize;
import com.leontran.themobilevn.model.JsonDataReturnListProduct;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.ListCitiProvinceData;
import com.leontran.themobilevn.model.ListSimpleObjectData;
import com.leontran.themobilevn.model.SimpleObjectType;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

import java.util.ArrayList;

public class CustomizeSearchActivity extends BasicActivity
        implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x3;

    private RelativeLayout btnGeo, btnGroupPrice, btnProduct, btnNearLocation;
    private LinearLayout layoutGeo, layoutGroupPrice, layoutProduct, layoutNearLocation;
    private Spinner spnProvince, spnDistrict, spnPriceGroup, spnProduct;
    private RadioButton raAll, raMobile, raTablet;
    private EditText edtInputName, edtRadius;

    private int openGeoLayout = 0;
    private int openGroupPrice = 0;
    private int openProduct = 0;
    private int openNearLocation = 0;

    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private ListCitiProvinceData arrCustomCiti;
    private ArrayList<SimpleObjectType> arrayCustomDistrict;
    private ListSimpleObjectData arrCustomPriceRange, arrCustomBrand, arrDeviceType;

    private String idProvince = "", idDistrict ="", idBrand ="" ,idPriceRange="" , idDeviceType="" ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_search_page);
        arrCustomCiti = MPreferenceManager.getCitiesProvinceList();
        arrDeviceType = MPreferenceManager.getDeviceType();
        initSlidingMenu();
        initLayout();
        initAdapter();
        setNavigationItemHightLight(0, 1);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppIndex.API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initLayout() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        edtInputName = (EditText) findViewById(R.id.edt_mobile_name);
        edtRadius = (EditText) findViewById(R.id.edt_radius);

        raAll = (RadioButton) findViewById(R.id.rd_all);
        raMobile = (RadioButton) findViewById(R.id.rd_mobile);
        raTablet = (RadioButton) findViewById(R.id.rd_tablet);

        Button btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);

        btnGeo = (RelativeLayout) findViewById(R.id.btn_geo);
        btnGeo.setOnClickListener(this);
        btnGroupPrice = (RelativeLayout) findViewById(R.id.btn_price_group);
        btnGroupPrice.setOnClickListener(this);
        btnProduct = (RelativeLayout) findViewById(R.id.btn_product);
        btnProduct.setOnClickListener(this);
        btnNearLocation = (RelativeLayout) findViewById(R.id.btn_near_location);
        btnNearLocation.setOnClickListener(this);
        layoutGeo = (LinearLayout) findViewById(R.id.layout_geo);
        layoutGroupPrice = (LinearLayout) findViewById(R.id.layout_price_group);
        layoutProduct = (LinearLayout) findViewById(R.id.layout_product);
        layoutNearLocation = (LinearLayout) findViewById(R.id.layout_near_location);
        spnProvince = (Spinner) findViewById(R.id.spn_province);
        spnDistrict = (Spinner) findViewById(R.id.spn_district);
        spnPriceGroup = (Spinner) findViewById(R.id.spn_price_group);
        spnProduct = (Spinner) findViewById(R.id.spn_product);

        ImageView btnQL = (ImageView) findViewById(R.id.btn_question_location);
        btnQL.setOnClickListener(this);
        ImageView btn = (ImageView) findViewById(R.id.btn_refresh);
        btn.setOnClickListener(this);

        raAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    idDeviceType = "";
                }

            }
        });
        raMobile.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (arrDeviceType !=null && isChecked){
                    idDeviceType = arrDeviceType.getArrayData().get(0).getId();
                }

            }
        });
        raTablet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (arrDeviceType !=null && isChecked){
                    idDeviceType = arrDeviceType.getArrayData().get(1).getId();
                }
            }
        });
    }

    private void initAdapter() {

        ArrayList<SimpleObjectType> arrayCustomCity = new ArrayList<SimpleObjectType>();
        for (int i = 0; i < arrCustomCiti.getArrayData().size(); i++) {
            SimpleObjectType data = new SimpleObjectType(arrCustomCiti.getArrayData().get(i).getId(), arrCustomCiti.getArrayData().get(i).getName());
            arrayCustomCity.add(data);
        }
        arrayCustomCity.add(0, new SimpleObjectType("", "Chưa Chọn"));
        CustomSpinnerAdapter adapterSPN = new CustomSpinnerAdapter(this, arrayCustomCity);
        spnProvince.setAdapter(adapterSPN);
        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    idProvince = arrCustomCiti.getArrayData().get(position - 1).getId();
                    arrayCustomDistrict = new ArrayList<SimpleObjectType>();
                    for (int i = 0; i < arrCustomCiti.getArrayData().get(position - 1).getListDistrict().size(); i++) {
                        SimpleObjectType data = new SimpleObjectType(arrCustomCiti.getArrayData().get(position - 1).getListDistrict().get(i).getId(), arrCustomCiti.getArrayData().get(position - 1).getListDistrict().get(i).getName());
                        arrayCustomDistrict.add(data);
                    }
                    arrayCustomDistrict.add(0, new SimpleObjectType("", "Chưa Chọn"));
                    CustomSpinnerAdapter adapterSPN = new CustomSpinnerAdapter(CustomizeSearchActivity.this, arrayCustomDistrict);
                    spnDistrict.setAdapter(adapterSPN);
                } else {
                    idProvince = "";
                    arrayCustomDistrict = new ArrayList<SimpleObjectType>();
                    arrayCustomDistrict.add(0, new SimpleObjectType("", "Chưa Chọn Tỉnh/Thành Phố"));
                    CustomSpinnerAdapter adapterSPN = new CustomSpinnerAdapter(CustomizeSearchActivity.this, arrayCustomDistrict);
                    spnDistrict.setAdapter(adapterSPN);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        arrayCustomDistrict = new ArrayList<SimpleObjectType>();
        arrayCustomDistrict.add(0, new SimpleObjectType("", "Chưa Chọn Tỉnh/Thành Phố"));
        adapterSPN = new CustomSpinnerAdapter(this, arrayCustomDistrict);
        spnDistrict.setAdapter(adapterSPN);
        spnDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idDistrict = arrayCustomDistrict.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        arrCustomPriceRange = MPreferenceManager.getPriceRange();
        arrCustomPriceRange.getArrayData().add(0, new SimpleObjectType("", "Chưa Chọn"));
        adapterSPN = new CustomSpinnerAdapter(this, arrCustomPriceRange.getArrayData());
        spnPriceGroup.setAdapter(adapterSPN);
        spnPriceGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idPriceRange = arrCustomPriceRange.getArrayData().get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        arrCustomBrand = MPreferenceManager.getBrand();
        arrCustomBrand.getArrayData().add(0, new SimpleObjectType("", "Chưa Chọn"));
        adapterSPN = new CustomSpinnerAdapter(this, arrCustomBrand.getArrayData());
        spnProduct.setAdapter(adapterSPN);
        spnProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idBrand = arrCustomBrand.getArrayData().get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_geo:
                if (openGeoLayout == 0) {
                    openGeoLayout = 1;
                    expand(layoutGeo);
                    ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_geo);
                    imgExtends.setImageResource(R.drawable.ic_arrow_up_gray);
                } else {
                    openGeoLayout = 0;
                    collapse(layoutGeo);
                    ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_geo);
                    imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
                }
                break;
            case R.id.btn_price_group:
                if (openGroupPrice == 0) {
                    openGroupPrice = 1;
                    expand(layoutGroupPrice);
                    ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_price_group);
                    imgExtends.setImageResource(R.drawable.ic_arrow_up_gray);
                } else {
                    openGroupPrice = 0;
                    collapse(layoutGroupPrice);
                    ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_price_group);
                    imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
                }
                break;
            case R.id.btn_product:
                if (openProduct == 0) {
                    openProduct = 1;
                    expand(layoutProduct);
                    ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_product);
                    imgExtends.setImageResource(R.drawable.ic_arrow_up_gray);
                } else {
                    openProduct = 0;
                    collapse(layoutProduct);
                    ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_product);
                    imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
                }
                break;
            case R.id.btn_near_location:
                if (mLastLocation != null) {
                    if (openNearLocation == 0) {
                        openNearLocation = 1;
                        expand(layoutNearLocation);
                        ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
                        imgExtends.setImageResource(R.drawable.ic_arrow_up_gray);
                    } else {
                        openNearLocation = 0;
                        collapse(layoutNearLocation);
                        ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
                        imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
                    }
                } else {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
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
                                                                    CustomizeSearchActivity.this, REQUEST_CHECK_SETTINGS);
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
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.dialog_location_notice)).setPositiveButton("Mở", dialogClickListener)
                            .setNegativeButton("Không", dialogClickListener).show();
                }
                break;
            case R.id.btn_question_location:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.dialog_location_notice)).setPositiveButton("Đóng", dialogClickListener)
                        .show();
                break;
            case R.id.btn_refresh:
                reSelectAll();
                break;
            case R.id.btn_submit:
                submitData();
                break;
        }
        super.onClick(v);
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
                                if (ContextCompat.checkSelfPermission(CustomizeSearchActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                            mGoogleApiClient);
                                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                                            mGoogleApiClient);
                                    if (mLastLocation == null) {
                                        ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
                                        imgExtends.setVisibility(View.INVISIBLE);
                                    } else {
                                        ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
                                        imgExtends.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }, 10000);
                        break;
                    case Activity.RESULT_CANCELED:

                        break;
                    default:
                        break;
                }
                break;

        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation == null) {
                ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
                imgExtends.setVisibility(View.INVISIBLE);
            } else {
                ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
                imgExtends.setVisibility(View.VISIBLE);
            }
            return;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    int targetHeight = 0;
    int initialHeight = 0;
    int timeRunanimation = 0;
    public View view;

    public void expand(View v) {
        view = v;
        view.setVisibility(View.VISIBLE);
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, 0);
        targetHeight = view.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        view.getLayoutParams().height = 1;
        view.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.getLayoutParams().height = interpolatedTime == 1
                        ? DrawerLayout.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                view.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        timeRunanimation = (int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density) * 4;
        a.setDuration(timeRunanimation);
        v.startAnimation(a);
    }

    public void collapse(View v) {
        view = v;
        initialHeight = view.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    view.setVisibility(View.GONE);
                } else {
                    view.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    view.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density) * 4);
        v.startAnimation(a);
    }

    private void reSelectAll() {
        edtInputName.setText("");
        edtRadius.setText("");
        raAll.setChecked(true);
        spnProvince.setSelection(0);
        spnDistrict.setSelection(0);
        spnPriceGroup.setSelection(0);
        spnProduct.setSelection(0);
        ImageView imgExtends = (ImageView) findViewById(R.id.img_extend_geo);
        imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
        imgExtends = (ImageView) findViewById(R.id.img_extend_price_group);
        imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
        imgExtends = (ImageView) findViewById(R.id.img_extend_product);
        imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
        imgExtends = (ImageView) findViewById(R.id.img_extend_near_location);
        imgExtends.setImageResource(R.drawable.ic_arrow_down_gray);
        collapse(layoutGeo);
        if (openGroupPrice == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    collapse(layoutGroupPrice);
                }
            }, timeRunanimation + 300);
        }

        if (openProduct == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    collapse(layoutProduct);
                }
            }, (timeRunanimation + 300) * 2);
        }
        if (openNearLocation == 1) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    collapse(layoutNearLocation);
                }
            }, (timeRunanimation + 300) * 3);
        }

        openNearLocation = 0;
        openProduct = 0;
        openGroupPrice = 0;
        openGeoLayout = 0;

    }

    private void submitData(){
        String textSearch = edtInputName.getText().toString().trim();
        String textRadius = edtRadius.getText().toString().trim();
        GenericLoadListProductCustomize dataGet = new GenericLoadListProductCustomize();
        dataGet.setPage(0);
        dataGet.setLimit(100);
        dataGet.setKeyword(textSearch);
        dataGet.setCity_id(idProvince);
        dataGet.setDistrict_id(idDistrict);
        dataGet.setPrice_range_id(idPriceRange);
        dataGet.setDevice_type_id(idDeviceType);
        dataGet.setBrand_id(idBrand);
        if (!textRadius.equals("")){
            dataGet.setCurrent_lat(mLastLocation.getLatitude() + "");
            dataGet.setCurrent_lng(mLastLocation.getLongitude() + "");
            dataGet.setRadius(textRadius);
        }
        MPreferenceManager.setCustomizeSearch(MPreferenceManager.getJsonFromObject(dataGet));
        Intent intentDetail = new Intent(CustomizeSearchActivity.this, CustomizeSearchResultActivity.class);
        startActivity(intentDetail);
        overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }
}
