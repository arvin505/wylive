package com.miqtech.wymaster.wylive.http;

import java.util.Map;

/**
 * Created by xiaoyi on 2016/8/15.
 * 网络请求抽象层
 */
public interface Request {

    void post(String url, Map<String, String> parmams, String method, ResponseListener callback,String requestTag);

    void remove(String tag);

    void cancleAll(String tag);
}
