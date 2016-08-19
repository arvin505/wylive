package com.miqtech.wymaster.wylive.utils;

import android.util.Log;

import com.miqtech.wymaster.wylive.BuildConfig;

/**
 * Created by xiaoyi on 2016/8/15.
 * LogUtil
 */
public class L {
    /**
     * BuildConfig.DEBUG debug版本为true
     * 打包出的版本为false
     */
    public static boolean debug = BuildConfig.DEBUG;

    public static void e(String tag, Object object) {
        if (debug) {
            if (object == null) {
                Log.e(tag, "----------------------object is null----------------------------");
            }
            try {
                Log.e(tag, object.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
