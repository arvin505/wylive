package com.miqtech.wymaster.wylive.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.miqtech.wymaster.wylive.WYLiveApp;


/**
 * Created by xiaoyi on 2016/6/24.
 * ToastUtils
 */
public class ToastUtils {
    private final static String TAG = "ToastUtils";
    private static Context mContext = WYLiveApp.getContext();
    private static Resources mResources = mContext.getResources();
    private volatile static Toast mToast;

    public static void show(String msg, int duration) {
        if (mToast == null) {
            synchronized (ToastUtils.class) {
                if (mToast == null) {
                    mToast = Toast.makeText(mContext, msg, duration);
                }
            }
        }
        mToast.setDuration(duration);
        mToast.setText(msg);
        mToast.show();
    }

    public static void show(int resId, int duration) {
        String msg = mResources.getString(resId);
        show(msg, duration);
    }


    public static void show(String msg) {
        show(msg, Toast.LENGTH_SHORT);
    }

    public static void show(int resId) {
        show(mResources.getString(resId), Toast.LENGTH_SHORT);
    }

}
