package com.leontran.find2buy.activity;

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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
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
import com.leontran.find2buy.R;
import com.leontran.find2buy.adapter.ListDeviceAdapter;
import com.leontran.find2buy.model.DeviceData;

import java.util.ArrayList;


public class HomePageActivity extends BasicActivity
        implements  View.OnClickListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x3;

    private SwipeRefreshLayout swipeContainer;
    private ListView lvData;
    private ImageView btnRefresh;
    private ArrayList<DeviceData> listDeivceResult;
    private ListDeviceAdapter mAdapter;


    public HomePageActivity() {
    }

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        initSlidingMenu();
        initLayout();
        initAdapter();

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
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvData = (ListView) findViewById(R.id.list_product);

        SearchView searchView = (SearchView) findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(HomePageActivity.this, query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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
        listDeivceResult = new ArrayList<>();
        listDeivceResult.add(new DeviceData(1, "HTC ONE M9", "3000000", 5));
        listDeivceResult.add(new DeviceData(2, "IPHONE 6 PLUS 64GB", "9000000", 3));
        listDeivceResult.add(new DeviceData(3, "SONY XPERIA Z3", "6000000", 2));
        listDeivceResult.add(new DeviceData(4, "SAMSUNG GALAXY S7", "17000000", 1));
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
                Uri.parse("android-app://com.leontran.find2buy.find2buy/http/host/path")
        );

    }

    @Override
    public void onStop() {
        super.onStop();

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
                Uri.parse("android-app://com.leontran.find2buy.find2buy/http/host/path")
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:

                break;
            case R.id.layout_main:
                DeviceData data = (DeviceData) v.getTag();
                Intent intentDetail = new Intent(HomePageActivity.this, ListStoreAcitivty.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }
}
