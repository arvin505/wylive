package com.miqtech.wymaster.wylive.http;

import android.support.annotation.NonNull;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.miqtech.wymaster.wylive.WYLiveApp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xiaoyi on 2016/8/15.
 */
public class VolleyRequest implements Request {
    private RequestQueue mRequestQueue;
    private Map<String, List<NormalPostRequest>> requestMap = new HashMap<>();
    private Object object = new Object();

    @Override
    public void post(String url, Map<String, String> parmams, final String method, @NonNull final ResponseListener callback, String requetTag) {
        NormalPostRequest request = new NormalPostRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("code") && response.getInt("code") == 0) {   //接口请求成功
                        callback.onSuccess(response, method);
                    } else {
                        callback.onFaild(response, method);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError(volleyError.getMessage(), method);
            }
        }, parmams);
        request.setRetryPolicy(new DefaultRetryPolicy(4 * 1000, 0, 1.0f));   //设置超时，重发
        request.setTag(requetTag);
        mRequestQueue.add(request);
        addRequestTag(request, requetTag);
    }

    /**
     * 将request加入到集合中
     * 同步代码块保证线程安全
     *
     * @param request
     * @param tag
     */
    private void addRequestTag(NormalPostRequest request, String tag) {
        if (requestMap.containsKey(request.getTag())) {
            requestMap.get(tag).add(request);
        } else {
            synchronized (object) {
                if (requestMap.containsKey(tag)) {
                    requestMap.get(tag).add(request);
                } else {
                    List<NormalPostRequest> normalPostRequests = new ArrayList<>();
                    normalPostRequests.add(request);
                    requestMap.put(tag, normalPostRequests);
                }
            }
        }
    }

    @Override
    public void remove(String tag) {
        if (requestMap.containsKey(tag)) {
            List<NormalPostRequest> requests = requestMap.get(tag);
            for (NormalPostRequest request : requests) {
                request.mListener = null;
                request.mErrorListener = null;
                request.mParams = null;
                request = null;
            }
            requestMap.remove(tag);
            requests = null;
        }
    }


    private static final class VolleyRequestHolder {
        private static final VolleyRequest sInstance = new VolleyRequest();
    }

    private VolleyRequest() {
        mRequestQueue = Volley.newRequestQueue(WYLiveApp.getContext());
    }

    public static VolleyRequest getInstance() {
        return VolleyRequestHolder.sInstance;
    }

    @Override
    public void cancleAll(String tag) {
        mRequestQueue.cancelAll(tag);
    }
}
