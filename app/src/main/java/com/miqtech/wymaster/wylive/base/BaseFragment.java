package com.miqtech.wymaster.wylive.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.http.Requestutil;
import com.miqtech.wymaster.wylive.http.ResponseListener;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
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
    protected BaseAppCompatActivity mActivity;
    protected View convertView;

    @BindView(R.id.ibLeft)
    @Nullable
    ImageButton btnBack;
    @BindView(R.id.tvLeftTitle)
    @Nullable
    TextView tvLeftTitle;
    @Nullable
    @BindView(R.id.img_exception)
    ImageView imgError;
    @Nullable
    @BindView(R.id.tvExceptionHint)
    TextView tvError;
    @Nullable
    @BindView(R.id.errorPage)
    LinearLayout errorPage;

    @Override
    public final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseAppCompatActivity) getActivity();
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (convertView == null) {
            int layoutId = getClass().getAnnotation(LayoutId.class).value();
            if (layoutId != -1) {
                convertView = inflater.inflate(layoutId, container, false);
            }
        }
        ButterKnife.bind(this, convertView);

        return convertView;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle("");
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

    public void sendHttpRequest(String url, Map<String, String> params) {
        showLoading("加载中...");
        StringBuilder builder = new StringBuilder(API.HOST);
        Requestutil.sendPostRequest(builder.append(url).toString(), params, url, this, TAG);
    }

    @CallSuper
    @Override
    public void onSuccess(JSONObject object, String method) {
        hideLoading();
        L.e(TAG, "-------------------------------------onSuccess----------------------------------------\n"
                + "--------------------------------------" + method + "---------------------------------------\n"
                + "data : " + object.toString());
    }

    @CallSuper
    @Override
    public void onError(String errMsg, String method) {
        L.e(TAG, "---------------------------------------onError-------------------------------\n"
                + "-------------------------------------" + method + "---------------------------------\n"
                + "data : " + errMsg);
        showLoading("加载失败！");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                    }
                });
            }
        }, 300);
    }

    @CallSuper
    @Override
    public void onFaild(JSONObject object, String method) {
        L.e(TAG, "-------------------------------onFaild------------------------------\n"
                + "----------------------------" + method + "----------------------------------\n"
                + "data : " + object.toString());
        hideLoading();
        try {
            if (object.getInt("code") == -1) {
                // FIXME: 2016/8/17
                if (!unNecessaryLogin(method)) {
                    showToast(object.getString("result"));
                    jumpToActivityForResult(LoginActivity.class, 1);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    /**
     * 设置标题
     * 在activity中默认使用注解的形式
     * 对于有些界面可能需要动态设置标题（如资讯详情等）
     * 提供一个protected 方法
     *
     * @param customTitle 标题
     */
    protected void setTitle(String customTitle) {
        if (tvLeftTitle == null) return;
        if (getClass().getAnnotation(Title.class) != null) {
            int titleId = getClass().getAnnotation(Title.class).titleId();
            String title = getClass().getAnnotation(Title.class).title();
            if (titleId != -1) {
                tvLeftTitle.setText(getResources().getText(titleId));
            }
            if (!title.equals("")) {
                tvLeftTitle.setText(title);
            }
        }
        if (!customTitle.equals("")) {
            tvLeftTitle.setText(customTitle);
        }
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


    private boolean unNecessaryLogin(String api) {
        List<String> apis = Arrays.asList(getActivity().getResources().getStringArray(R.array.login_unnecessary));
        if (apis.contains(api)) return true;
        return false;
    }

    protected void showLoading(String str) {
        mActivity.showLoading(str);
    }

    protected void hideLoading() {
        mActivity.hideLoading();
    }

    protected void showErrorPage(String errMsg, @DrawableRes int resID) {
        errorPage.setVisibility(View.VISIBLE);
        if (errMsg != null) {
            tvError.setText(errMsg);
        }
        if (resID != 0) {
            imgError.setImageResource(resID);
        }
    }

    protected void hideErrorPage() {
        errorPage.setVisibility(View.GONE);
    }
}