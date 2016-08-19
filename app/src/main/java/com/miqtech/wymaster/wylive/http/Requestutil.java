package com.miqtech.wymaster.wylive.http;

import android.support.annotation.NonNull;

import com.miqtech.wymaster.wylive.utils.L;

import java.util.Map;
import java.util.MissingResourceException;

/**
 * Created by xiaoyi on 2016/8/15.
 */
public class Requestutil {
    private static final String TAG = "Requestutil";
    private static Request mRequest;

    private Requestutil() {

    }

    static {
        mRequest = VolleyRequest.getInstance();
    }

    public static void sendPostRequest(String url, Map<String, String> params, String method, @NonNull ResponseListener callback, String requestTag) {
        mRequest.post(url, params, method, callback, requestTag);
        generateUrlString(url,params);
    }

    /**
     * 销毁时移除引用
     * 避免内存溢出
     */
    public static void remove(String tag) {
        mRequest.remove(tag);
    }

    public static void cancleAll(String tag) {
        mRequest.cancleAll(tag);
    }

    private static void generateUrlString(String url, Map<String, String> params) {
        if (params==null){
            L.e(TAG, "--------url------------" + "\n"+url);
            return;
        }
        if (url.endsWith("?"))
            url = url.substring(0, url.length() - 1);
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        String urlStr = sb.toString().substring(0, sb.length() - 1);
        L.e(TAG, "--------url------------" + "\n" + urlStr);
    }
}
