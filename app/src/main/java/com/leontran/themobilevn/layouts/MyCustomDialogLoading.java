package com.leontran.themobilevn.layouts;

import android.content.Context;
import android.view.Window;
import android.widget.LinearLayout;

import com.leontran.themobilevn.R;


/**
 * Created by NguyenTa.Tran on 4/5/2016.
 */
public class MyCustomDialogLoading extends MyCustomDialogParent {

    boolean isLoading;

    public MyCustomDialogLoading(Context context) {
        super(context, R.style.ThemeDialogCustom);
        // TODO Auto-generated constructor stub
        getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_loading);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
    }

    public MyCustomDialogLoading(Context context, boolean loading) {
        super(context, R.style.ThemeDialogCustom);
        getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_dialog_loading);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        isLoading = loading;
    }



    @Override
    public void cancel() {
        // TODO Auto-generated method stub
        isLoading = false;
        super.cancel();
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        isLoading = false;
        super.dismiss();
    }

}


