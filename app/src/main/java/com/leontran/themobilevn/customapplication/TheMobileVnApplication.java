package com.leontran.themobilevn.customapplication;

import android.app.Application;
import android.support.multidex.MultiDex;

import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;

/**
 * Created by NguyenTa.Tran on 8/16/2016.
 */
public class TheMobileVnApplication  extends Application {

    // FB ID
    private static final String APP_ID = "1631498300473927";
    private static final String APP_NAMESPACE = "com.leontran.themobilevn";


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        MultiDex.install(this);
        super.onCreate();
        Permission[] permissions = new Permission[]{Permission.PUBLIC_PROFILE, Permission.EMAIL,
                Permission.USER_FRIENDS};

        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId(APP_ID).setNamespace(APP_NAMESPACE)
                .setPermissions(permissions)
                .setAskForAllPermissionsAtOnce(true).build();

        SimpleFacebook.setConfiguration(configuration);

    }
}
