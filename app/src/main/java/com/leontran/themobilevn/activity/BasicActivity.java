package com.leontran.themobilevn.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.layouts.MyCustomDialogLoading;
import com.leontran.themobilevn.model.GenericPostLogin;
import com.leontran.themobilevn.model.GenericPostSignOut;
import com.leontran.themobilevn.model.JsonDataReturnListProduct;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.UserProfileData;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnLogoutListener;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 4/28/2016.
 */
public class BasicActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {


    public DrawerLayout mDrawerLayout;
    public Toolbar toolbar;
    private MyCustomDialogLoading myDialogLoading;
    public ImageLoader mImgLoader;
    public DisplayImageOptions options;
    public boolean isRefresh = false;
    public SimpleFacebook mSimpleFacebook;

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
                            UIUtils.showToast(BasicActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            postSignOut(mDataDownloader, false);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(BasicActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_SIGN_OUT_POST){
                        JsonDataReturnListProduct gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnListProduct.class);
                        } catch (Exception e) {
                            UIUtils.showToast(BasicActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setUserProfile("");
                            Intent intentDetail = new Intent(BasicActivity.this, HomePageActivity.class);
                            intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentDetail);
                            finishAffinity();
                            overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(BasicActivity.this,
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
        MPreferenceManager.setContext(this);
        mImgLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        if (mSimpleFacebook == null) {
            mSimpleFacebook = SimpleFacebook.getInstance(this);
        }
    }

    public void initSlidingMenu(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View v = navigationView.getHeaderView(0);
        Button btnSignIn = (Button) v.findViewById(R.id.btn_sign_in);
        btnSignIn.setOnClickListener(this);
        ImageView btnSetting = (ImageView) v.findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(this);
        UserProfileData userData = MPreferenceManager.getUserProfile();
        if (userData != null){
            TextView txtName = (TextView) v.findViewById(R.id.txt_name);
            txtName.setText(userData.getName());
            TextView txtEmail = (TextView) v.findViewById(R.id.txt_email);
            txtEmail.setText(userData.getEmail());
            btnSignIn.setVisibility(View.GONE);
            btnSetting.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(true);
        } else {
            btnSignIn.setVisibility(View.VISIBLE);
            btnSetting.setVisibility(View.GONE);
            navigationView.getMenu().findItem(R.id.nav_sign_out).setVisible(false);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        mSimpleFacebook.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void doLogout() {
        mSimpleFacebook.logout(new OnLogoutListener() {


            @Override
            public void onLogout() {
                // TODO Auto-generated method stub
                mSimpleFacebook.clean();
                mSimpleFacebook = SimpleFacebook.getInstance(BasicActivity.this);
            }
        });
    }

    public void doLogin(OnLoginListener onLoginListener) {
        showDialogLoading();
        mSimpleFacebook.login(onLoginListener);
    }

    public void showDialogLoading() {
        if (myDialogLoading == null) {
            myDialogLoading = new MyCustomDialogLoading(this);
        }
        if (myDialogLoading != null && !myDialogLoading.isShowing()) {
            myDialogLoading.show();
        }
    }

    public void hideDialogLoading() {
        if (myDialogLoading != null && myDialogLoading.isShowing()) {
            myDialogLoading.dismiss();
        }
    }

    public boolean isShowDialogLoading() {
        if (myDialogLoading != null && myDialogLoading.isShowing()) {
            return true;
        }
        return false;
    }


    public void setNavigationItemHightLight(int group , int position){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(group).getSubMenu().getItem(position).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id){
            case R.id.nav_quick_search:
                Intent intentDetail = new Intent(BasicActivity.this, HomePageActivity.class);
                intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentDetail);
                finishAffinity();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.nav_customize_search:
                intentDetail = new Intent(BasicActivity.this, CustomizeSearchActivity.class);
                intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentDetail);
                finishAffinity();
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.nav_sign_up:
                intentDetail = new Intent(BasicActivity.this, SignInActivity.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.nav_sign_out:
                postLogin(mDataDownloader, false);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_sign_in:
                Intent intentDetail = new Intent(BasicActivity.this, SignInActivity.class);
                intentDetail.putExtra(SignInActivity.REQUEST_TAB , SignInActivity.TAB_SIGN_IN);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            case R.id.btn_setting:
                intentDetail = new Intent(BasicActivity.this, UpdateProfileActivity.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
        }
    }

    public void postLogin(DataDownloader mDataDownloader, boolean isRefresh){
        GenericPostLogin dataPost = new GenericPostLogin();
        if (MPreferenceManager.isConnectionAvailable(this)) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_LOGIN_STRING, json,
                    RequestDataFactory.ACTION_LOGIN);
            if (mDataDownloader != null) {
                mDataDownloader.ExitTask();
                mDataDownloader.addRequestJson(data1, data1.params, DataDownloader.METHOD_POST);
                if (!isRefresh){
                    showDialogLoading();
                }
            }
        }
    }

    public void postSignOut(DataDownloader mDataDownloader, boolean isRefresh){
        UserProfileData data = MPreferenceManager.getUserProfile();
        GenericPostSignOut dataPost = new GenericPostSignOut();
        dataPost.setEmail(data.getEmail());
        dataPost.setUser_access_token(data.getUser_access_token());
        dataPost.setToken(MPreferenceManager.getAccessToken());
        if (MPreferenceManager.isConnectionAvailable(this)) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_SIGN_OUT_STRING, json,
                    RequestDataFactory.ACTION_SIGN_OUT_POST);
            if (mDataDownloader != null) {
                mDataDownloader.ExitTask();
                mDataDownloader.addRequestJson(data1, data1.params, DataDownloader.METHOD_POST);
                if (!isRefresh){
                    showDialogLoading();
                }
            }
        }
    }

}
