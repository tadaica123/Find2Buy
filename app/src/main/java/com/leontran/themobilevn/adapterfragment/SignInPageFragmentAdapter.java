package com.leontran.themobilevn.adapterfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.leontran.themobilevn.fragment.SignInFragment;
import com.leontran.themobilevn.fragment.SignUpFragment;

import java.util.ArrayList;

/**
 * Created by NguyenTa.Tran on 8/14/2016.
 */
public class SignInPageFragmentAdapter extends FragmentStatePagerAdapter {

    private int mCount;

    public SignInPageFragmentAdapter(FragmentManager fm) {
        super(fm);
        mCount  = 2;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0){
            return new SignUpFragment();
        } else {
            return new SignInFragment();
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

}
