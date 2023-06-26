package com.hante.smartpadposclient;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.hjq.toast.ToastUtils;


public class HantePOSApplication extends Application {
    public static HantePOSApplication application;

    private static final Handler sHandler = new Handler();
    private static Context mContext;
    /**
     * 蓝牙程序 application
     */
    public static Context getContext() {
        return mContext;
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
        mContext = getApplicationContext();
        //初始化 toast
        ToastUtils.init(application);

    }


}
