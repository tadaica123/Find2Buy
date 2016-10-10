package com.leontran.themobilevn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;
import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.adapterfragment.SignInPageFragmentAdapter;
import com.leontran.themobilevn.layouts.MyCustomViewPager;
import com.leontran.themobilevn.model.GenericPostLoginFB;
import com.leontran.themobilevn.model.GenericPostRegister;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.JsonDataReturnSignIn;
import com.leontran.themobilevn.model.UserProfileData;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import java.util.List;

/**
 * Created by NguyenTa.Tran on 8/9/2016.
 */
public class SignInActivity extends BasicActivity implements View.OnClickListener {

    public static String REQUEST_TAB = "tab";
    public static int TAB_SIGN_IN = 1;
    public static int TAB_SIGN_UP = 0;

    private int currentTag = 0;

    private MyCustomViewPager mPager;
    private  TextView txtTitle;
    private Button btnChange;
    private SignInPageFragmentAdapter mAdapter;
    private Profile fbProfile;

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
                            UIUtils.showToast(SignInActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            signInFb(fbProfile);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SignInActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_SIGN_IN_FB_POST){
                        JsonDataReturnSignIn gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSignIn.class);
                        } catch (Exception e) {
                            UIUtils.showToast(SignInActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            UIUtils.showToast(SignInActivity.this , getResources().getString(R.string.sign_in_success));

                            UserProfileData userData = new UserProfileData();
                            userData.setEmail(fbProfile.getEmail());
                            userData.setName(gr.getName());
                            userData.setUser_access_token(gr.getUser_access_token());
                            MPreferenceManager.setUserProfile(MPreferenceManager.getJsonFromObject(userData));
                            Intent intentDetail = new Intent(SignInActivity.this, HomePageActivity.class);
                            intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intentDetail);
                            finishAffinity();
                           overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);

                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(SignInActivity.this,
                                        gr.getMessage());
                            }
                        }
                    }
                    hideDialogLoading();
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
        setContentView(R.layout.activity_sign_in);
        initLayout();
    }

    private void initLayout(){
        mPager = (MyCustomViewPager) findViewById(R.id.pager);
        mPager.setPagingEnabled(false);
        mAdapter = new SignInPageFragmentAdapter(getSupportFragmentManager());
        mPager.setAdapter(mAdapter);
        txtTitle = (TextView) findViewById(R.id.txt_title);
        btnChange = (Button) findViewById(R.id.btn_next_page);
        btnChange.setOnClickListener(this);
        ImageView btnBack  = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        Button btnSignInFB  = (Button) findViewById(R.id.btn_sign_in_fb);
        btnSignInFB.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            if (bundle.containsKey(REQUEST_TAB)){
                if (bundle.getInt(REQUEST_TAB) == 0){
                    currentTag = TAB_SIGN_UP;
                    mPager.setCurrentItem(currentTag);
                    txtTitle.setText(getResources().getString(R.string.nav_menu_sign_up));
                    btnChange.setText(getResources().getString(R.string.sign_in));
                } else {
                    currentTag = TAB_SIGN_IN;
                    mPager.setCurrentItem(currentTag);
                    txtTitle.setText(getResources().getString(R.string.sign_in));
                    btnChange.setText(getResources().getString(R.string.nav_menu_sign_up));
                }
            }
        }

    }

    final OnLoginListener onLoginListener = new OnLoginListener() {


        @Override
        public void onLogin(String accessToken, List<Permission> acceptedPermissions, List<Permission> declinedPermissions) {
            getProfileFB();
        }

        @Override
        public void onCancel() {
            UIUtils.showToast(SignInActivity.this, "Cancel FB");
        }

        @Override
        public void onFail(String reason) {
            hideDialogLoading();
            UIUtils.showToast(SignInActivity.this, reason);
        }

        @Override
        public void onException(Throwable throwable) {
            UIUtils.showToast(SignInActivity.this, "Error FB");
        }
    };

    private void getProfileFB() {
        showDialogLoading();
        Profile.Properties properties = new Profile.Properties.Builder().add(Profile.Properties.ID)
                .add(Profile.Properties.NAME).add(Profile.Properties.FIRST_NAME).add(Profile.Properties.LAST_NAME)
                .add(Profile.Properties.EMAIL).build();
        mSimpleFacebook.getProfile(properties, new OnProfileListener() {
            @Override
            public void onComplete(Profile response) {
                // TODO Auto-generated method stub
                super.onComplete(response);
                fbProfile = response;
                hideDialogLoading();
                postLogin(mDataDownloader, false);
            }

            @Override
            public void onFail(String reason) {
                // TODO Auto-generated method stub
                super.onFail(reason);
                hideDialogLoading();
                UIUtils.showToast(SignInActivity.this, reason);
            }
        });
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_next_page:
                if (currentTag == TAB_SIGN_IN){
                    currentTag = TAB_SIGN_UP;
                    mPager.setCurrentItem(currentTag);
                    txtTitle.setText(getResources().getString(R.string.nav_menu_sign_up));
                    btnChange.setText(getResources().getString(R.string.sign_in));
                } else {
                    currentTag = TAB_SIGN_IN;
                    mPager.setCurrentItem(currentTag);
                    txtTitle.setText(getResources().getString(R.string.sign_in));
                    btnChange.setText(getResources().getString(R.string.nav_menu_sign_up));
                }
                break;
            case R.id.btn_sign_in_fb:
                if (!mSimpleFacebook.isLogin()) {
                    mSimpleFacebook.login(onLoginListener);
                } else {
                    getProfileFB();
                }
                break;
            default:
                break;
        }
    }

    private void signInFb(Profile profile){
        GenericPostLoginFB dataPost = new GenericPostLoginFB();
        dataPost.setToken(MPreferenceManager.getAccessToken());
        dataPost.setEmail(profile.getEmail());
        dataPost.setName(profile.getName());
        dataPost.setFacebook_id(profile.getId());
        dataPost.setFb_access_token(mSimpleFacebook.getToken());
        dataPost.setFacebook_username("");
        if (MPreferenceManager.isConnectionAvailable(this)) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_SIGN_IN_FB_STRING, json,
                    RequestDataFactory.ACTION_SIGN_IN_FB_POST);
            if (mDataDownloader != null) {
                mDataDownloader.ExitTask();
                mDataDownloader.addRequestJson(data1, data1.params, DataDownloader.METHOD_POST);
                showDialogLoading();
            }
        }
    }


}
