package com.miqtech.wymaster.wylive.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.miqtech.wymaster.wylive.WYLiveApp;

/**
 * Created by xiaoyi on 2016/8/18.
 * sharepreference工具类
 * 将wymaster中繁杂的代码简化
 * 因为之前缓存变量的代码很多都是新增一个方法，
 * 但是其存储逻辑均使用了相同的代码，所以完全没必要
 * 增加几百行繁杂的代码
 */
public class PreferenceUtils {
    private static SharedPreferences preferences = getPreferences();

    private PreferenceUtils() {
    }

    public static void putString(String key, String value) {
        preferences.edit().putString(key, value);
    }

    public static String getString(String key) {
        return preferences.getString(key, "");
    }

    public static void putInt(String key, int value) {
        preferences.edit().putInt(key, value);
    }

    public static int getInt(String key) {
        return preferences.getInt(key, -1);
    }

    public static void putBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value);
    }

    public static boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public static void putLong(String key, long value) {
        preferences.edit().putLong(key, value);
    }

    public static double getLong(String key) {
        return preferences.getLong(key, -1);
    }

    public static SharedPreferences getPreferences() {
        Context context = WYLiveApp.getContext();
        if (preferences == null) {
            synchronized (PreferenceUtils.class) {
                if (preferences == null) {
                    preferences = PreferenceManager.getDefaultSharedPreferences(context);
                }
            }
        }
        return preferences;
    }
}
