package com.miqtech.wymaster.wylive.module.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.WYLiveApp;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.constants.Constants;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.register.RegisterActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.MyAlertView;
import com.miqtech.wymaster.wylive.widget.PhoneTextWatcher;
import com.miqtech.wymaster.wylive.widget.WeiboLoginLinearLayout;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by zhaosentao on 2016/8/22.
 */
@LayoutId(R.layout.activity_login)
public class LoginActivity extends BaseAppCompatActivity implements TextWatcher, MyAlertView.VerificationCodeDialogAction {

    @BindView(R.id.loginRlPhone)
    RelativeLayout rlPhone;
    @BindView(R.id.loginEtPhone)
    EditText etPhone;
    @BindView(R.id.loginIvClearPhone)
    ImageView ivClearPhone;

    @BindView(R.id.loginRlCode)
    RelativeLayout rlCode;
    @BindView(R.id.loginEtCode)
    EditText etCode;
    @BindView(R.id.loginTvSendCode)
    TextView tvSendCode;

    @BindView(R.id.loginRlPassword)
    RelativeLayout rlPassword;
    @BindView(R.id.loginTvPassword)
    TextView tvPassword;//忘记密码
    @BindView(R.id.loginEtPassword)
    EditText etPassword;

    @BindView(R.id.loginTvSwitching)
    TextView tvSwitching;//切换登陆方式
    @BindView(R.id.loginBtLogin)
    Button btLogin;
    @BindView(R.id.rightTitle)
    TextView btRegister;
    @BindView(R.id.back)
    LinearLayout back;

    @BindView(R.id.loginLlWeixin)
    LinearLayout llWeixin;
    @BindView(R.id.loginLlQQ)
    LinearLayout llQQ;
    @BindView(R.id.loginLlWeibo)
    WeiboLoginLinearLayout llWeibo;

    private Context context;
    private boolean isPassword = false;//是否是密码登陆

    private Drawable drawableDown;
    private Drawable drawableUp;
    private String passwordLogin;//使用密码登陆
    private String codeLogin;//使用验证码登录
    private String toObtain;//重新获取
    private String frequentClick;//频繁点击

    private String imputPhone;//输入的电话
    private String imputPassword;//输入的密码
    private String imputCode;//输入的验证码
    private String imputServicerCode;//服务器验证码

    private boolean isNeedServiceCode = false;//是否需要服务器验证码

    private Animation animiationShaking;

    private Timer timer;
    private int totalTime = 60;
    private MyHandler myHandler;
    private MyTimerTask task;

    private PhoneTextWatcher watcher;
    private MyAlertView dialog;
    private View dialogRlServiceCode;
    private View dialogTvYes;
    private View dialogIvServiceCode;
    private View dialogEtServiceCode;

        /*微博*/
    /**
     * 登陆认证对应的listener
     */
    private AuthListener mLoginListener = new AuthListener();
    private AuthInfo mAuthInfo;
    /*QQ*/
    private BaseUiListener uiListener = new BaseUiListener();
    private Tencent mTencent;
    /*微信*/
    private IWXAPI api;

    private int platform = 3;//1qq2微信3微博,必传.默认为3
    private final static int LOGIN_QQ = 1;
    private final static int LOGIN_WECHAT = 2;
    private final static int LOGIN_WEIBO = 3;
    private String openId;//第三方登陆标识

    @Override
    protected void initViews(Bundle savedInstanceState) {
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        drawableDown = getResources().getDrawable(R.drawable.login_switch_down);
        drawableUp = getResources().getDrawable(R.drawable.login_switch_up);
        drawableDown.setBounds(0, 0, drawableDown.getMinimumWidth(), drawableDown.getMinimumHeight());
        drawableUp.setBounds(0, 0, drawableUp.getMinimumWidth(), drawableUp.getMinimumHeight());

        passwordLogin = getResources().getString(R.string.please_use_password_to_login);
        codeLogin = getResources().getString(R.string.please_use_code_to_login);
        toObtain = getResources().getString(R.string.to_obtain);
        frequentClick = getResources().getString(R.string.please_do_not_frequent_click);

        animiationShaking = AnimationUtils.loadAnimation(context, R.anim.shaking);

        watcher = new PhoneTextWatcher(etPhone, ivClearPhone, getResources().getString(R.string.please_imput_phone), btLogin, tvSendCode);
        etPhone.addTextChangedListener(watcher);

        etCode.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        tvSendCode.setEnabled(false);
        loginIsClick(false);
        configurationForLogin();
    }

    /**
     * 第三方登陆配置
     */
    private void configurationForLogin() {
        /*微博*/
        // 创建授权认证信息
        mAuthInfo = new AuthInfo(getApplicationContext(), Constants.WB_APP_KEY, Constants.WB_REDIRECTURL, Constants.WB_APP_SCOPE);
        llWeibo.setWeiboAuthInfo(mAuthInfo, mLoginListener);
        //微信api注册
        api = WXAPIFactory.createWXAPI(getApplicationContext(), Constants.WX_APP_ID, true);
        api.registerApp(Constants.WX_APP_ID);
        //QQ注册
        mTencent = Tencent.createInstance(Constants.QQ_APP_ID, getApplicationContext());
    }

    @OnClick({R.id.loginTvSendCode, R.id.loginTvSwitching, R.id.loginBtLogin, R.id.rightTitle, R.id.loginTvPassword, R.id.back,
            R.id.loginLlWeixin, R.id.loginLlQQ, R.id.loginLlWeibo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginTvSendCode://发送验证码
                if (isPhoneNull()) {
                    return;
                }
                if (isNeedServiceCode) {
                    dialog = new MyAlertView.Builder(context).creatImgVerificationCode(imputPhone, this);
                } else {
                    timeStart();
                    sendSMSCodeMobile();
                }
                break;
            case R.id.loginTvPassword://忘记密码
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);//1是注册2是找回密码
                jumpToActivity(RegisterActivity.class, bundle);
                break;
            case R.id.loginTvSwitching://切换登陆方式
                switching();
                break;
            case R.id.loginBtLogin://登陆
                goToLogin();
                break;
            case R.id.rightTitle://注册新用户
                Bundle bundle1 = new Bundle();
                bundle1.putInt("type", 1);
                jumpToActivityForResult(RegisterActivity.class, 10, bundle1);
//                Bundle bundle1 = new Bundle();
//                bundle1.putString("url", API.HOST + API.YUER_AGREEMENT);
//                jumpToActivity(SubjectActivity.class, bundle1);
                break;
            case R.id.back:
                onBackPressed();
                break;
            case R.id.loginLlWeixin:
                thirdLogin(LOGIN_WECHAT);
                break;
            case R.id.loginLlQQ:
                thirdLogin(LOGIN_QQ);
                break;
            case R.id.loginLlWeibo:
                platform = 3;
                break;

        }
    }

    /**
     * 发送短信验证码
     */
    private void sendSMSCodeMobile() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", imputPhone);
        map.put("type", "4");//1:发送注册的验证码2:发送找回密码的验证码4快捷登陆验证码5第三方登录验证码
        if (isNeedServiceCode) {//2两小时获取验证码超过3次,需要输入图片验证码
            map.put("code", imputServicerCode);
        }
        sendHttpRequest(API.SEND_SMS_CODE_MOBILE, map);
    }

    private void goToLogin() {
        imputPhone = etPhone.getText().toString();
        imputPhone = imputPhone.replaceAll("\\s*", "");
        imputCode = etCode.getText().toString();
        imputPassword = etPassword.getText().toString();

        if (!imputPhone.matches(Constants.PHONE_FORMAT)) {//校验手机号码
            showToast(getResources().getString(R.string.phone_format_is_not_correct));
            return;
        }

        if (isPassword) {//密码或者验证码的非空判断
            if (imputPassword == null) {
                showToast(getResources().getString(R.string.please_imput_password));
                return;
            }
        } else {
            if (imputCode == null) {
                showToast(getResources().getString(R.string.please_imput_code));
                return;
            }
        }

        Map<String, String> map = new HashMap();
        map.put("username", imputPhone);
        map.put("source", "1");
        if (isPassword) {//密码登陆
            map.put("type", "1");
            map.put("password", imputPassword);
        } else {//验证码登陆
            map.put("checkCode", imputCode);
            map.put("type", "2");
        }
        sendHttpRequest(API.LOGIN, map);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        switch (method) {
            case API.LOGIN:
                loginSuccess(object);
                break;
            case API.SEND_SMS_CODE_MOBILE:
                if (dialog != null) {
                    dialog.dismiss();
                }
                showToast(getResources().getString(R.string.code_is_sended));
                break;
            case API.THIRD_LOGIN:
                toBindingOrtoMain(object);
                break;

        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(getResources().getString(R.string.noNeteork));
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            switch (method) {
                case API.LOGIN:
                    if (object.has("result")) {
                        showToast(object.getString("result").toString());
                    } else {
                        showToast(getResources().getString(R.string.login_fail));
                    }
                    break;
                case API.SEND_SMS_CODE_MOBILE:
                    if (object.getInt("code") == 2) {//2两小时获取验证码超过3次,需要输入图片验证码
                        isNeedServiceCode = true;
                        dialog = new MyAlertView.Builder(context).creatImgVerificationCode(imputPhone, this);
                    } else if (object.getInt("code") == 6) {//服务器验证码错误
                        dialogRlServiceCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rectangle_corner_solid_06));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialogRlServiceCode.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_rectangle_corner_solid_05));
                                ((EditText) dialogEtServiceCode).setText("");
                                AsyncImage.loadYZM(API.HOST + API.IMAGE_CODE_REGISTER + imputPhone, (ImageView) dialogIvServiceCode);
                            }
                        }, 1100);
                        dialogRlServiceCode.startAnimation(animiationShaking);
                        dialogTvYes.setEnabled(false);
                        dialogTvYes.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.dialog_button_pri));
                        ((TextView) dialogTvYes).setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.hall_live_item_bg));
                    } else {
                        if (object.has("result")) {
                            showToast(object.getString("result").toString());
                        }
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (watcher.isFullPhone()) {
            if (etPassword.isFocused() && etPassword.getText().length() > 0) {
                loginIsClick(true);
            } else if (etCode.isFocused() && etCode.getText().length() == 6) {
                loginIsClick(true);
            } else {
                loginIsClick(false);
            }
        } else {
            tvSendCode.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void doPositive(String verificationCode) {
        if (verificationCode == null || TextUtils.isEmpty(verificationCode)) {
            showToast("请输入图片验证码");
            return;
        }
        imputServicerCode = verificationCode;
        sendSMSCodeMobile();
    }

    @Override
    public void doNegative() {

    }

    @Override
    public void changeView(View view1, View view2, View view3, View view4) {
        dialogRlServiceCode = view1;
        dialogTvYes = view2;
        dialogIvServiceCode = view3;
        dialogEtServiceCode = view4;
    }

    /**
     * 登陆成功后
     *
     * @param object
     */
    private void loginSuccess(JSONObject object) {
        try {
            User user = new Gson().fromJson(object.getString("object"), User.class);
            UserProxy.setUser(user);
            setResult(RESULT_OK);
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换登陆方式
     */
    private void switching() {
        if (Utils.isFastDoubleClick()) {
            showToast(frequentClick);
            return;
        }
        if (!isPassword) {//切换到密码登陆
            tvSwitching.setCompoundDrawables(drawableUp, null, null, null);
            tvSwitching.setText(codeLogin);
            rlCode.setVisibility(View.GONE);
            rlPassword.setVisibility(View.VISIBLE);
            isPassword = true;
        } else {
            tvSwitching.setCompoundDrawables(drawableDown, null, null, null);
            tvSwitching.setText(passwordLogin);
            rlCode.setVisibility(View.VISIBLE);
            rlPassword.setVisibility(View.GONE);
            isPassword = false;
        }
    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            totalTime--;
            myHandler.sendEmptyMessage(0);
        }
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (totalTime > 0) {
                tvSendCode.setText(toObtain + "(" + totalTime + "s)");
                tvSendCode.setEnabled(false);
            } else {
                tvSendCode.setEnabled(true);
                tvSendCode.setText(toObtain);
                timeOut();
            }
        }
    }

    private void timeOut() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    /**
     * 倒计时开始
     */
    private void timeStart() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new MyTimerTask();
        }

        if (myHandler == null) {
            myHandler = new MyHandler();
        }
        totalTime = 60;
        timer.schedule(task, 1000, 1000);
        tvSendCode.setText(toObtain + "(" + totalTime + "s)");
    }

    /**
     * 登陆按钮是否可点击
     *
     * @param isClick
     */
    private void loginIsClick(boolean isClick) {
        if (isClick) {
            btLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button));
            btLogin.setEnabled(true);
            btLogin.setTextColor(getResources().getColor(R.color.white));
        } else {
            btLogin.setEnabled(false);
            btLogin.setBackgroundDrawable(getResources().getDrawable(R.drawable.register_button));
            btLogin.setTextColor(getResources().getColor(R.color.text_gray_one));
        }
    }

    private boolean isPhoneNull() {
        boolean isPhoneNot = false;
        imputPhone = etPhone.getText().toString();
        if (imputPhone == null || imputPhone.isEmpty()) {//校验手机号码
            showToast(getResources().getString(R.string.please_imput_phone));
            isPhoneNot = true;
            return isPhoneNot;
        }
        imputPhone = imputPhone.replaceAll("\\s*", "");
        if (!imputPhone.matches(Constants.PHONE_FORMAT)) {
            showToast(getResources().getString(R.string.phone_format_is_not_correct));
            isPhoneNot = true;
            return isPhoneNot;
        }
        return isPhoneNot;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 10) {
            int registerOk = data.getIntExtra("register_ok", -1);
            if (registerOk == 1) {
                setResult(RESULT_OK);
                finish();
            }
        } else {
            if (platform == LOGIN_QQ) {
//                if (requestCode == com.tencent.connect.common.Constants.REQUEST_API) {//QQ登陆的回调
//                    mTencent.onActivityResult(requestCode, resultCode, data,uiListener);
                    mTencent.onActivityResultData(requestCode,resultCode,data,uiListener);
//                }
            } else if (platform == LOGIN_WEIBO) {//微博
                llWeibo.onActivityResult(requestCode, resultCode, data);//微博登陆的回调
            } else if (platform == LOGIN_WECHAT) {//微信
            }
        }
    }


    /**
     * 第三方登陆
     *
     * @param loginType 1qq2微信3微博,必传
     */
    private void thirdLogin(int loginType) {
        platform = loginType;
        switch (loginType) {
            case LOGIN_QQ:
//                if (!mTencent.isSessionValid()) {
                platform = LOGIN_QQ;
                mTencent.login(this, "get_user_info", uiListener);
//                    mTencent.login(this, "all", uiListener);
//                }
                break;
            case LOGIN_WECHAT:
                SendAuth.Req req = new SendAuth.Req();
                //授权读取用户信息
                req.scope = "snsapi_userinfo";
                //自定义信息
                req.state = "wangyudashi";
                //向微信发送请求
                api.sendReq(req);
                platform = LOGIN_WEIBO;
//                this.finish();
                break;
            case LOGIN_WEIBO:
                break;
        }
    }

    /**
     * 微博：登入按钮的监听器，接收授权结果。
     */
    private class AuthListener implements WeiboAuthListener {
        @Override
        public void onComplete(Bundle values) {
            Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(values);
            if (accessToken != null && accessToken.isSessionValid()) {
                openId = accessToken.getUid();
                thirdLoginToVerification();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            showToast(R.string.weibo_login_error);
        }

        @Override
        public void onCancel() {
            showToast(R.string.weibo_login_cancel);
        }
    }

    /**
     * QQ登陆实例回调
     */
    private class BaseUiListener implements IUiListener {
        @Override
        public void onComplete(Object o) {
            try {
                JSONObject object = new JSONObject(o.toString());
                if (object.has("openid")) {
                    openId = object.getString("openid");
                }
                thirdLoginToVerification();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            showToast(R.string.qq_login_error);
        }

        @Override
        public void onCancel() {
            showToast(R.string.qq_login_cancel);
        }
    }

    /**
     * 第三方登陆后后台验证
     */
    private void thirdLoginToVerification() {
        Map<String, String> map = new HashMap<>();
        map.put("openId", openId);
        map.put("platform", platform + "");//1qq2微信3微博,必传
        map.put("source", 1 + "");//	娱儿tv调用时传1,网娱大师不用传
        sendHttpRequest(API.THIRD_LOGIN, map);
    }

    /**
     * 判断是否去绑定手机号还是直接登陆跳主页
     *
     * @param object
     */
    private void toBindingOrtoMain(JSONObject object) {
        Intent intent = null;
        try {
            if (!object.has("object")) {//如果第三方登录未绑定手机号,则返回的object为空
                showToast("等等成功");
//                intent = new Intent(context, MobileLoginOrBindingActivity.class);
//                intent.putExtra("platform", platform);//	1qq2微信3微博,必传
//                intent.putExtra("openId", openId);
//                intent.putExtra("type", 1);
//                startActivity(intent);
//                finish();
            } else {
                User user = new Gson().fromJson(object.getString("object").toString(), User.class);
                showToast("登录成功");
                UserProxy.setUser(user);
                setResult(RESULT_OK);
                finish();
            }
            platform = LOGIN_WEIBO;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
