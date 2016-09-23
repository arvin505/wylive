package com.miqtech.wymaster.wylive.module.register;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
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
import com.miqtech.wymaster.wylive.module.main.ui.activity.MainActivity;
import com.miqtech.wymaster.wylive.module.main.ui.activity.SubjectActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.MyAlertView;
import com.miqtech.wymaster.wylive.widget.MyPagerView;
import com.miqtech.wymaster.wylive.widget.PhoneTextWatcher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 注册/找回密码
 * Created by zhaosentao on 2016/8/24.
 */
@LayoutId(R.layout.activity_register)
public class RegisterActivity extends BaseAppCompatActivity implements TextWatcher {

    @BindView(R.id.rightTitle)
    TextView tvRightTitle;
    @BindView(R.id.back)
    LinearLayout back;
    @BindView(R.id.title)
    TextView tvTitle;
    @BindView(R.id.myPagerView)
    MyPagerView myPagerView;
    private int mPosition;

    private String toObtain;//重新获取
    private String frequentClick;//频繁点击
    private String imputPhone;//输入的电话
    private String imputCode;//输入的验证码
    private String serviceCode;//图片验证码
    private String imputPassword;//密码
    private Context context;

    private Timer timer;
    private int totalTime = 60;
    private MyHandler myHandler;
    private MyTimerTask task;
    private int type;//1表示注册，2表示忘记密码

    private List<View> views = new ArrayList<>();

    private View phoneView;//输入手机号码页面
    private EditText etPhone;
    private ImageView ivClearPhone;
    private Button btGetCode;

    private View codeView;//输入验证码页面
    private TextView tvSendCode;
    private EditText etCode;
    private TextView tvTip;
    private Button btNext;

    private View passwordView;//输入密码页面
    private RelativeLayout rlIsShowPassword;
    private ImageView ivIsShowPassword;
    private EditText etPassword;
    private Button btFinish;
    private LinearLayout registerLlAgreement;//用户协议跳转

    private RigsterPagerAdapter adapter;
    private LayoutInflater inflater;
    private MyAlertView dialog;
    private Animation animiationShaking;
    private View dialogRlServiceCode;
    private View dialogTvYes;
    private View dialogIvServiceCode;
    private View dialogEtServiceCode;
    private DialogAction dialogAction;
    private boolean isShowPassword = false;

    private MyAlertView hasRegisterDialog;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        context = this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        type = getIntent().getIntExtra("type", 1);
        if (type == 1) {//1是注册2是找回密码
            tvTitle.setText(getResources().getString(R.string.register_user));
        } else if (type == 2) {
            tvTitle.setText(getResources().getString(R.string.retrieve_password));
        }
        toObtain = getResources().getString(R.string.to_obtain);
        frequentClick = getResources().getString(R.string.please_do_not_frequent_click);
        animiationShaking = AnimationUtils.loadAnimation(context, R.anim.shaking);
        dialogAction = new DialogAction();
        addView();
        myPagerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                if (position == 0) {
                    tvRightTitle.setVisibility(View.GONE);
                } else {
                    tvRightTitle.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.rightTitle, R.id.back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rightTitle://重新选择手机号码
                if (mPosition == 1) {
                    myPagerView.setCurrentItem(0);
                } else if (mPosition == 2) {
                    myPagerView.setCurrentItem(1);
                }
                timeOut();
                break;
            case R.id.back:
                onBackPressed();
                break;
        }
    }

    private void addView() {
        ViewPagerOnclick viewPagerOnclick = new ViewPagerOnclick();

        inflater = LayoutInflater.from(context);

        phoneView = inflater.inflate(R.layout.layout_register_phone, null);
        etPhone = (EditText) phoneView.findViewById(R.id.registerEtPhone);
        ivClearPhone = (ImageView) phoneView.findViewById(R.id.registerIvClearPhone);
        btGetCode = (Button) phoneView.findViewById(R.id.registerBtGetCode);

        codeView = inflater.inflate(R.layout.layout_register_code, null);
        tvSendCode = (TextView) codeView.findViewById(R.id.registerTvSendCode);
        etCode = (EditText) codeView.findViewById(R.id.registerEtCode);
        tvTip = (TextView) codeView.findViewById(R.id.registerTvTip);
        btNext = (Button) codeView.findViewById(R.id.registerBtNext);

        passwordView = inflater.inflate(R.layout.layout_register_password, null);
        rlIsShowPassword = (RelativeLayout) passwordView.findViewById(R.id.registerRlCode);
        ivIsShowPassword = (ImageView) passwordView.findViewById(R.id.registerTvIsShowPassword);
        etPassword = (EditText) passwordView.findViewById(R.id.registerEtPassword);
        btFinish = (Button) passwordView.findViewById(R.id.registerBtFinish);
        registerLlAgreement = (LinearLayout) passwordView.findViewById(R.id.registerLlAgreement);
        if (type == 1) {//注册
            registerLlAgreement.setVisibility(View.VISIBLE);
        } else {
            registerLlAgreement.setVisibility(View.GONE);
        }
        etPhone.addTextChangedListener(new PhoneTextWatcher(etPhone, ivClearPhone, getResources().getString(R.string.please_imput_phone), btGetCode, null));
        etCode.addTextChangedListener(this);
        etPassword.addTextChangedListener(this);
        btGetCode.setOnClickListener(viewPagerOnclick);
        btNext.setOnClickListener(viewPagerOnclick);
        btFinish.setOnClickListener(viewPagerOnclick);
        tvSendCode.setOnClickListener(viewPagerOnclick);
        rlIsShowPassword.setOnClickListener(viewPagerOnclick);
        registerLlAgreement.setOnClickListener(viewPagerOnclick);
        btGetCode.setEnabled(false);
        btNext.setEnabled(false);
        btFinish.setEnabled(false);

        views.add(phoneView);
        views.add(codeView);
        views.add(passwordView);

        adapter = new RigsterPagerAdapter(context, views);
        myPagerView.setAdapter(adapter);
    }

    /**
     * 发送短信验证码
     */
    private void sendSMSCodeMobile() {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", imputPhone);
        map.put("type", type + "");//1:发送注册的验证码2:发送找回密码的验证码4快捷登陆验证码5第三方登录验证码
        map.put("code", serviceCode);
        sendHttpRequest(API.SEND_SMS_CODE_MOBILE, map);
    }

    /**
     * 校验验证码
     */
    private void checkSMSCode() {
        isFastFoubleClick();
        imputCode = etCode.getText().toString().trim();
        if (imputCode == null || TextUtils.isEmpty(imputCode)) {
            showToast(getResources().getString(R.string.please_imput_code));
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", imputPhone);
        map.put("type", type + "");
        map.put("checkCode", imputCode);
        sendHttpRequest(API.CHECK_SMS_CODE, map);
    }

    /**
     * 去注册或者找回密码
     */
    private void registerOrRetrievePassword() {
        isFastFoubleClick();
        imputPassword = etPassword.getText().toString();
        if (imputPassword.length() < 6) {
            showToast(getResources().getString(R.string.please_enter_password_at_least));
            return;
        }

        Map<String, String> map = new HashMap<>();
        map.put("mobile", imputPhone);
        map.put("password", imputPassword);
        if (type == 1) {
            map.put("smsCode", imputCode);
            map.put("source", "1");//娱儿tv调用时传1,网娱大师不用传
            map.put("androidChannelName", DeviceUtils.getMetaData(context, "UMENG_CHANNEL"));
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            map.put("deviceId", DEVICE_ID);
            sendHttpRequest(API.REGISTER, map);
        } else if (type == 2) {
            map.put("code", imputCode);
            sendHttpRequest(API.RESET_PASSWORD, map);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        switch (method) {
            case API.SEND_SMS_CODE_MOBILE:
                if (dialog != null) {
                    dialog.dismiss();
                }
                showToast(getResources().getString(R.string.code_is_sended));
                myPagerView.setCurrentItem(1);
                timeStart();
                tvTip.setText(addconnent(getResources().getString(R.string.code_has_bean_sended),
                        Utils.changeString(imputPhone),
                        getResources().getString(R.string.please_look_at)));
                break;
            case API.CHECK_SMS_CODE://校验短信验证码通过
                myPagerView.setCurrentItem(2);
                timeOut();
                break;
            case API.REGISTER://注册
                try {
                    User user = new Gson().fromJson(object.getString("object"), User.class);
                    UserProxy.setUser(user);
//                    Intent intent = new Intent();
//                    intent.setClass(context, MainActivity.class);
//                    intent.putExtra("registerType",1);
//                    startActivity(intent);
                    Intent intent = new Intent();
                    intent.putExtra("register_ok", 1);
                    setResult(RESULT_OK, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case API.RESET_PASSWORD://找回密码
                onBackPressed();
                showToast(getResources().getString(R.string.please_login_again));
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
                case API.SEND_SMS_CODE_MOBILE:
                    if (object.getInt("code") == 6) {//服务器验证码错误
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
                    } else if (object.getInt("code") == 2) {//号码已被注册
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                        hasRegisterDialog = new MyAlertView.Builder(context).creatHasRegistered(new MyAlertView.DialogAction() {
                            @Override
                            public void doPositive() {
                                etPhone.setText("");
                            }

                            @Override
                            public void doNegative() {
                            }
                        });
                    } else {
                        if (object.has("result")) {
                            showToast(object.getString("result").toString());
                        }
                    }
                    break;
                default:
                    if (object.has("result")) {
                        showToast(object.getString("result").toString());
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class ViewPagerOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.registerBtGetCode://获取验证码，进入到填写验证码界面
                    creatServierCodeDialog();
                    break;
                case R.id.registerBtNext://校验验证码,进入到填写密码界面
                    checkSMSCode();
                    break;
                case R.id.registerBtFinish://注册完成
                    registerOrRetrievePassword();
                    break;
                case R.id.registerTvSendCode://获取验证码
                    dialog = new MyAlertView.Builder(context).creatImgVerificationCode(imputPhone, dialogAction);
//                    timeStart();
                    break;
                case R.id.registerRlCode://显示隐藏密码
                    showOrHidePassword();
                    break;
                case R.id.registerLlAgreement://用户协议
                    Bundle bundle = new Bundle();
                    bundle.putString("url", API.HOST + API.YUER_AGREEMENT);
                    jumpToActivity(SubjectActivity.class, bundle);
                    break;
            }
        }
    }

    /**
     * 显示或者隐藏密码
     */
    private void showOrHidePassword() {
        if (isShowPassword) {
            isShowPassword = false;
            ivIsShowPassword.setImageResource(R.drawable.password_invisible);
            etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else {
            isShowPassword = true;
            ivIsShowPassword.setImageResource(R.drawable.password_visible);
            etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        String textPassword = etPassword.getText().toString();
        if (!TextUtils.isEmpty(textPassword)) {
            etPassword.setSelection(textPassword.length());
        }
    }

    /**
     * 创建图片验证码的弹框
     */
    private void creatServierCodeDialog() {
        isFastFoubleClick();
        imputPhone = etPhone.getText().toString();
        if (imputPhone == null || imputPhone.isEmpty()) {//校验手机号码
            showToast(getResources().getString(R.string.please_imput_phone));
            return;
        }

        imputPhone = imputPhone.replaceAll("\\s*", "");

        if (!imputPhone.matches(Constants.PHONE_FORMAT)) {
            showToast(getResources().getString(R.string.phone_format_is_not_correct));
            return;
        }
        dialog = new MyAlertView.Builder(context).creatImgVerificationCode(imputPhone, dialogAction);
    }

    /**
     * 弹框的点击事件
     */
    class DialogAction implements MyAlertView.VerificationCodeDialogAction {
        @Override
        public void doPositive(String verificationCode) {
            serviceCode = verificationCode;
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

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (etCode.isFocused()) {
            buttomCanClick(btNext, etCode);
        } else if (etPassword.isFocused()) {
            buttomCanClick(btFinish, etPassword);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    /**
     * 是否进行了快速点击
     */
    private void isFastFoubleClick() {
        if (Utils.isFastDoubleClick()) {
            showToast(frequentClick);
            return;
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
     * 按钮可点与不可点时  显示的状态
     *
     * @param button
     * @param editText
     */
    private void buttomCanClick(Button button, EditText editText) {
        int i = 0;
        if (button == btFinish) {
            i = 5;
        } else {
            i = 0;
        }

        if (editText.getText().length() > i) {
            button.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.login_button));
            button.setEnabled(true);
            button.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.white));
        } else {
            button.setEnabled(false);
            button.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.register_button));
            button.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.text_gray_one));
        }
    }

    /**
     * @param cotent1
     * @param content2
     * @param content3
     * @return
     */
    private SpannableStringBuilder addconnent(String cotent1, String content2, String content3) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(cotent1);
        int start = cotent1.length();
        int middle = start + content2.length();
        ssb.append(content2);
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.attention_item_count));// 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, start, middle, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb.append(content3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timeOut();
        if (dialog != null) {
            dialog.dismiss();
        }

        if (hasRegisterDialog != null) {
            hasRegisterDialog.dismiss();
        }
    }
}
