package com.leontran.themobilevn.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.leontran.themobilevn.activity.BasicActivity;
import com.leontran.themobilevn.utils.MPreferenceManager;

/**
 * Created by NguyenTa.Tran on 8/16/2016.
 */
public class BasicFragment extends Fragment {

    public BasicActivity mAct;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mAct = (BasicActivity) getActivity();
        MPreferenceManager.setContext(mAct);
        super.onCreate(savedInstanceState);

    }

    protected void hideDialogLoading() {
        if (mAct != null) {
            mAct.hideDialogLoading();
        }
    }

    protected void showDialogLoading() {
        if (mAct != null) {
            mAct.showDialogLoading();
        }
    }

    public boolean isShowDialogLoading() {
        if (mAct != null) {
            return mAct.isShowDialogLoading();
        }
        return false;
    }
}
