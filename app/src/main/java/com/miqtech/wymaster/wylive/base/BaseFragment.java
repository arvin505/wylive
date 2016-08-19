package com.miqtech.wymaster.wylive.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.http.Requestutil;
import com.miqtech.wymaster.wylive.http.ResponseListener;
import com.miqtech.wymaster.wylive.utils.ToastUtils;

import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/16.
 * Fragment 基类
 * 定义了通用部分
 * 类似于BaseAppCompatActivity
 * 布局文件以注解方式设置
 * 子类需要实现initViews()方法
 */
public abstract class BaseFragment extends Fragment implements ResponseListener {
    protected String TAG = getClass().getSimpleName();
    protected Activity mActivity;
    protected View convertView;

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (convertView == null) {
            int layoutId = getClass().getAnnotation(LayoutId.class).value();
            if (layoutId != -1) {
                convertView = inflater.inflate(layoutId, container, false);
            }
        }
        ButterKnife.bind(convertView);
        return convertView;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view, savedInstanceState);
    }

    protected abstract void initViews(View view, Bundle savedInstanceState);

    @Override
    public void onPause() {
        super.onPause();
        Requestutil.cancleAll(TAG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Requestutil.remove(TAG);
    }

    protected void setTitle(String title) {

    }

    protected void sendHttpRequest(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(API.HOST);
        Requestutil.sendPostRequest(builder.append(url).toString(), params, url, this, TAG);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {

    }

    @Override
    public void onError(String errMsg, String method) {

    }

    @Override
    public void onFaild(JSONObject object, String method) {

    }

    protected void jumpToActivity(@NonNull Class clazz) {
        jumpToActivity(clazz, null);
    }

    protected void jumpToActivity(@NonNull Class clazz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mActivity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    protected void jumpToActivityForResult(@Nullable Class clazz, int requestCode) {
        jumpToActivityForResult(clazz, requestCode, null);
    }

    protected void jumpToActivityForResult(@Nullable Class clazz, int requestCode, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mActivity, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    @UiThread
    protected void showToast(int resId) {
        ToastUtils.show(resId);
    }

    @UiThread
    protected void showToast(String msg) {
        ToastUtils.show(msg);
    }

    @UiThread
    protected void showToast(String msg, int duration) {
        ToastUtils.show(msg, duration);
    }

    @UiThread
    protected void showToast(int resId, int duration) {
        ToastUtils.show(resId, duration);
    }

}