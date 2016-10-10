package com.leontran.themobilevn.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.leontran.themobilevn.R;

/**
 * Created by NguyenTa.Tran on 5/10/2016.
 */
public class InformationDeivceActivity extends BasicActivity implements View.OnClickListener{

    public static final String HTML = "html";
    private String htmlString;
    private WebView wvContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_information_page);
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            if (bd.containsKey(HTML)) {
                htmlString = bd.getString(HTML);
            }
        }
        initLayout();
    }

    private void initLayout() {
        LinearLayout btnBack = (LinearLayout) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        wvContent = (WebView) findViewById(R.id.wv);
        initWebContent(htmlString);
    }

    private void initWebContent(String htmlData){
        String head = "<head><style>@font-face {font-family: 'verdana';src: url('file:///android_asset/fonts/FrutigerLTStd-Roman.otf');}body {font-family: 'verdana';}</style></head>";
        htmlData= "<html>"+head+"<body style=\"font-family: verdana\" ><font color=\'black\'>"+htmlData+"</font></body></html>" ;
        String mime = "text/html";
        String encoding = "utf-8";
//		htmlData = "<font color=\'white\'>" + htmlData + "</font>";
        wvContent.setBackgroundColor(0xffffffff);
        wvContent.loadDataWithBaseURL(null, htmlData, mime, encoding, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refresh:

                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
}
