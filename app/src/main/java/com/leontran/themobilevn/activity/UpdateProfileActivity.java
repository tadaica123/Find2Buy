package com.leontran.themobilevn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.GenericPostUpdateProfile;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.JsonDataReturnSignIn;
import com.leontran.themobilevn.model.UserProfileData;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

import java.util.List;

/**
 * Created by NguyenTa.Tran on 8/21/2016.
 */
public class UpdateProfileActivity extends BasicActivity implements View.OnClickListener {

    private EditText edtName, edtEmail, edtAddress, edtPhone;
    private String name, address, phone;

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
                            UIUtils.showToast(UpdateProfileActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            doPostUpdateProfile();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(UpdateProfileActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_UPDATE_PROFILE){
                        JsonDataReturnSignIn gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSignIn.class);
                        } catch (Exception e) {
                            UIUtils.showToast(UpdateProfileActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            UserProfileData data = MPreferenceManager.getUserProfile();
                            data.setName(name);
                            data.setPhone(phone);
                            data.setAddress(address);
                            UIUtils.showToast(UpdateProfileActivity.this, getResources().getString(R.string.update_success));
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(UpdateProfileActivity.this,
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
        setContentView(R.layout.activity_profile_setting);
        initLayout();
    }

    private void initLayout(){
        ImageView btnBack  = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        Button btn  = (Button) findViewById(R.id.btn_change_pw);
        btn.setOnClickListener(this);
        btn  = (Button) findViewById(R.id.btn_submit);
        btn.setOnClickListener(this);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        edtPhone = (EditText) findViewById(R.id.edt_phone);

        UserProfileData data = MPreferenceManager.getUserProfile();
        edtName.setText(data.getName());
        edtEmail.setText(data.getEmail());
        edtAddress.setText(data.getAddress());
        edtPhone.setText(data.getPhone());
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_submit:
                postLogin(mDataDownloader, false);
                break;
            case R.id.btn_change_pw:
                Intent intentDetail = new Intent(UpdateProfileActivity.this, ChangePassActivity.class);
                startActivity(intentDetail);
                overridePendingTransition(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                break;
            default:
                break;
        }
    }


    private void doPostUpdateProfile(){

        name = edtName.getText().toString().trim();
        phone = edtName.getText().toString().trim();
        address = edtAddress.getText().toString().trim();

        UserProfileData data = MPreferenceManager.getUserProfile();
        GenericPostUpdateProfile dataPost = new GenericPostUpdateProfile();
        dataPost.setEmail(data.getEmail());
        dataPost.setToken(MPreferenceManager.getAccessToken());
        dataPost.setUser_access_token(data.getUser_access_token());
        dataPost.setName(name);
        dataPost.setAddress(address);
        dataPost.setPhone_number(phone);
        if (MPreferenceManager.isConnectionAvailable(this)) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_UPDATE_PROFILE_STRING, json,
                    RequestDataFactory.ACTION_UPDATE_PROFILE);
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
