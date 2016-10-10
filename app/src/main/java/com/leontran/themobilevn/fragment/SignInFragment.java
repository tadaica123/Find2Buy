package com.leontran.themobilevn.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.activity.HomePageActivity;
import com.leontran.themobilevn.model.GenericPostRegister;
import com.leontran.themobilevn.model.JsonDataReturnBasic;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.model.JsonDataReturnSignIn;
import com.leontran.themobilevn.model.UserProfileData;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 8/14/2016.
 */
public class SignInFragment  extends BasicFragment implements View.OnClickListener {

    private static final String KEY_CONTENT = "SignInFragment:Content";

    private String mContent = "???";
    private View rootView;

    private EditText edtMail, edtPass;
    private Button btnSubmit;

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
                            UIUtils.showToast(mAct, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            MPreferenceManager.setAccessToken(gr.getToken());
                            signIn();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(mAct,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_SIGN_IN_POST){
                        JsonDataReturnSignIn gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnSignIn.class);
                        } catch (Exception e) {
                            UIUtils.showToast(mAct, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success

                            UIUtils.showToast(getActivity() , getResources().getString(R.string.sign_in_success));

                            UserProfileData userData = new UserProfileData();
                            userData.setEmail(edtMail.getText().toString());
                            userData.setName(gr.getName());
                            userData.setUser_access_token(gr.getUser_access_token());
                            MPreferenceManager.setUserProfile(MPreferenceManager.getJsonFromObject(userData));
                            Intent intentDetail = new Intent(getActivity(), HomePageActivity.class);
                            intentDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            getActivity().startActivity(intentDetail);
                            getActivity().finishAffinity();
                            getActivity().overridePendingTransition(android.R.anim.fade_in,
                                    android.R.anim.fade_out);
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(mAct,
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


    public SignInFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContent = savedInstanceState.getString(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sign_in,
                container, false);
        edtMail = (EditText) rootView.findViewById(R.id.edt_email);
        edtPass = (EditText) rootView.findViewById(R.id.edt_pass);
        btnSubmit = (Button) rootView.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, mContent);
    }

    private void signIn(){
        String email = edtMail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        GenericPostRegister dataPost = new GenericPostRegister();
        dataPost.setToken(MPreferenceManager.getAccessToken());
        dataPost.setEmail(email);
        dataPost.setPass(pass);
        if (MPreferenceManager.isConnectionAvailable(getActivity())) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_SIGN_IN_STRING, json,
                    RequestDataFactory.ACTION_SIGN_IN_POST);
            if (mDataDownloader != null) {
                mDataDownloader.ExitTask();
                mDataDownloader.addRequestJson(data1, data1.params, DataDownloader.METHOD_POST);
                showDialogLoading();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_submit:
                mAct.postLogin(mDataDownloader, false);
                break;
        }
    }
}
