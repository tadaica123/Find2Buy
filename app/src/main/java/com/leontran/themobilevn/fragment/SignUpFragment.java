package com.leontran.themobilevn.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leontran.themobilevn.R;
import com.leontran.themobilevn.model.GenericPostLogin;
import com.leontran.themobilevn.model.GenericPostRegister;
import com.leontran.themobilevn.model.JsonDataReturnBasic;
import com.leontran.themobilevn.model.JsonDataReturnLogin;
import com.leontran.themobilevn.networking.RequestDataFactory;
import com.leontran.themobilevn.utils.DataDownloader;
import com.leontran.themobilevn.utils.MPreferenceManager;
import com.leontran.themobilevn.utils.RequestData;
import com.leontran.themobilevn.utils.UIUtils;


/**
 * Created by NguyenTa.Tran on 8/14/2016.
 */
public class SignUpFragment extends BasicFragment implements View.OnClickListener {

    private static final String KEY_CONTENT = "SignUpFragment:Content";

    private String mContent = "???";
    private View rootView;

    private EditText edtName, edtMail, edtPass;
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
                            register();
                        } else {
                            // Error
                            if (gr != null && gr.getMessage() != null) {
                                UIUtils.showToast(mAct,
                                        gr.getMessage());
                            }
                        }
                    } else if (type == RequestDataFactory.ACTION_REGISTER_POST){
                        JsonDataReturnBasic gr = null;
                        try {
                            gr = gson.fromJson(result, JsonDataReturnBasic.class);
                        } catch (Exception e) {
                            UIUtils.showToast(mAct, "Server error");
                        }
                        if (result != null && gr != null && gr.isSuccess()) {
                            // Success
                            UIUtils.showToast(getActivity() , getResources().getString(R.string.register_success));
                            getActivity().finish();
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


    public SignUpFragment() {

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
        rootView = inflater.inflate(R.layout.fragment_sign_up,
                container, false);
        edtName = (EditText) rootView.findViewById(R.id.edt_name);
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

    private void register(){
        String name = edtName.getText().toString().trim();
        String email = edtMail.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        GenericPostRegister dataPost = new GenericPostRegister();
        dataPost.setName(name);
        dataPost.setToken(MPreferenceManager.getAccessToken());
        dataPost.setEmail(email);
        dataPost.setPass(pass);
        if (MPreferenceManager.isConnectionAvailable(getActivity())) {
            String json = MPreferenceManager.getJsonFromObject(dataPost);
            RequestData data1 = new RequestData(
                    RequestDataFactory.DEFAULT_DOMAIN_HOST + RequestDataFactory.ACTION_REGISTER_STRING, json,
                    RequestDataFactory.ACTION_REGISTER_POST);
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

