package com.leontran.themobilevn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.GenericPostChangePW;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.JsonDataReturnSignIn;
import com.leontran.themobilevn.model.UserProfileData;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;

/**
 * Created by NguyenTa.Tran on 8/21/2016.
 */
public class ChangePassActivity extends BasicActivity implements View.OnClickListener {

    private EditText edtOldPass, edtNewPass, edtConfirmPass;

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
                            UIUtils.showToast(ChangePassActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            doPostUpdatePW();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(ChangePassActivity.this,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_CHANGE_PW){
                        JsonDataReturnSignIn gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSignIn.class);
                        } catch (Exception e) {
                            UIUtils.showToast(ChangePassActivity.this, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            UIUtils.showToast(ChangePassActivity.this, getResources().getString(R.string.update_success));
                            finish();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(ChangePassActivity.this,
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
        setContentView(R.layout.activity_change_pw);
        initLayout();
    }

    private void initLayout(){

        ImageView btnBack  = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        Button btn  = (Button) findViewById(R.id.btn_submit);
        btn.setOnClickListener(this);
        edtOldPass = (EditText) findViewById(R.id.edt_old_pass);
        edtNewPass = (EditText) findViewById(R.id.edt_new_pass);
        edtConfirmPass = (EditText) findViewById(R.id.edt_renew_pass);
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
            default:
                break;
        }
    }

    private void doPostUpdatePW(){

        String OldPass = edtOldPass.getText().toString().trim();
        String NewPass = edtNewPass.getText().toString().trim();
        String ReNewPass = edtConfirmPass.getText().toString().trim();

        if (!NewPass.equals(ReNewPass)){
            UIUtils.showToast(ChangePassActivity.this,
                    getResources().getString(R.string.error_not_same_pw_confirm));
        }

        UserProfileData data = MPreferenceManager.getUserProfile();
        GenericPostChangePW dataPost = new GenericPostChangePW();
        dataPost.setEmail(data.getEmail());
        dataPost.setToken(MPreferenceManager.getAccessToken());
        dataPost.setUser_access_token(data.getUser_access_token());
        dataPost.setPassword(NewPass);
        dataPost.setPassword_confirmation(ReNewPass);
        if (MPreferenceManager.isConnectionAvailable(this)) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_CHANGE_PW_STRING, json,
                    RequestDataFactory.ACTION_CHANGE_PW);
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

