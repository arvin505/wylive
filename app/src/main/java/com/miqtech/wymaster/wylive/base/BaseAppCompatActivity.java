package com.miqtech.wymaster.wylive.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.http.Requestutil;
import com.miqtech.wymaster.wylive.http.ResponseListener;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.ToastUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/15.
 * Activity基类，抽象类
 * 定义了Activity通用部分
 *
 * @
 * @see {oncreate()}
 * 采用注解的方式设置布局文件ID@see{LayoutId.class}
 * 在Base中使用Butterknife注入
 * 将oncreate()方法设置为final(包含了初始化模板，不应被重写)
 * <p>
 * 其他说明：
 * @see {Title.class}
 * activity标题使用了注解的方式
 * 另外针对某些页面标题会动态改变的
 * 提供了一个方法@see{setTitle(String str)}
 * @see {showBack()}
 * <p>
 * 提供两个showBack()方法
 * 可以替换左上角back图标
 * 通常情况下不需要调用，也不需要设置点击事件
 * 对于某些界面不要显示back按钮的，需要在初始化的时候调用@see{hideBack()}
 * <p>
 * 子类应该实现@see{initViews(Bundle saveInstance)}
 * 需要注意的是：此BaseAppCompatActivity的初始化采用了模板方法模式
 * 子类无需关注初始化过程，只需要实现相应的方法即可
 * 请各位在开发的时候遵照上述协议
 * 如果有更好的建议也可以商讨之后修改
 * 另外，Base类里面的代码应该是通用的，所有子类都会或者潜在意义的会使用到
 * 所以，请各位在修改Base类的时候注意一下，对于不属于通用代码部分，尽可能
 * 不要写在Base中
 */
public abstract class BaseAppCompatActivity extends AppCompatActivity implements ResponseListener {

    @BindView(R.id.ibLeft)
    @Nullable
    ImageButton btnBack;
    @BindView(R.id.tvLeftTitle)
    @Nullable
    TextView tvLeftTitle;

    protected final String TAG = getClass().getSimpleName();

    @Override
    public final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int resId = getClass().getAnnotation(LayoutId.class).value();
        setContentView(resId);
        ButterKnife.bind(this);
        setTitle("");
        showBack();
        initViews(savedInstanceState);
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

    /**
     * 设置显示左上角back按钮的icon
     *
     * @param resId 资源id
     */
    protected final void showBack(@DrawableRes int resId) {
        if (btnBack == null) return;
        if (resId != -1) {
            btnBack.setImageResource(resId);
        }
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * 左上角back按钮显示默认icon
     */
    protected final void showBack() {
        showBack(-1);
    }

    /**
     * 隐藏back
     * 主页需要隐藏，其他默认打开
     */
    protected final void hideBack() {
        if (btnBack != null)
            btnBack.setVisibility(View.GONE);
    }

    protected abstract void initViews(Bundle savedInstanceState);


    /**
     * findview
     *
     * @param resId viewId
     * @param <T>
     * @return view
     */
    protected <T extends View> T findView(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * findview
     *
     * @param resId viewId
     * @param group ViewGroup
     * @param <T>
     * @return view
     */
    protected <T extends View> T findView(View group, int resId) {
        return (T) group.findViewById(resId);
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


    protected void sendHttpRequest(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(API.HOST);
        Requestutil.sendPostRequest(builder.append(url).toString(), params, url, this, TAG);
    }


    @Override
    protected void onPause() {
        super.onPause();
        Requestutil.cancleAll(TAG);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Requestutil.remove(TAG);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
       /* L.e(TAG, "----------success----   ");
        Gson gson = new Gson();
        try {
            User user = gson.fromJson(object.getJSONObject("object").toString(), User.class);
            L.e(TAG, "--------------   " + user.toString());
            UserProxy.setUser(user);
            User user2 = UserProxy.getUser();
            L.e(TAG, "--------user2------   " + user2.toString());
            user.setId("12121");
            UserProxy.setUser(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onError(String errMsg, String method) {

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        try {
            if (object.getInt("code") == -1) {
                showToast(object.getString("result"));
                // FIXME: 2016/8/17
                //跳转至登陆界面
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void jumpToActivity(@NonNull Class clazz) {
        L.e(TAG, "---jump------");
        jumpToActivity(clazz, null);
    }

    protected void jumpToActivity(@NonNull Class clazz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clazz);
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
        intent.setClass(this, clazz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }
}
