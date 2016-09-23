package com.miqtech.wymaster.wylive.module.wxapi;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.constants.Constants;
import com.miqtech.wymaster.wylive.entity.CompleteTask;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.main.ui.activity.SubjectActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.ShareToFriendsUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WXEntryActivity extends BaseAppCompatActivity implements IWXAPIEventHandler {

    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
    private String openid;
    private Context context;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID, false);
        api.handleIntent(getIntent(), this);
        context = this;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {

    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        int result = 0;
        if (resp instanceof SendAuth.Resp) {
            String code;
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = R.string.wechat_login_success;
                    SendAuth.Resp newResp = (SendAuth.Resp) resp;
                    //获取微信传回的code
                    code = newResp.code;
                    showToast(code);
                    Map<String, String> map = new HashMap<>();
                    map.put("appid", Constants.WX_APP_ID);
                    map.put("secret", Constants.WX_APP_SECRET);
                    map.put("code", code);
                    map.put("grant_type", "authorization_code");
                    sendHttpRequest(API.GET_USER_INFO_BY_WEIXIN_LOGIN, map);
                    showToast(result);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL://取消
                    result = R.string.wechat_login_cancel;
                    showToast(result);
                    finish();
                    return;
                case BaseResp.ErrCode.ERR_AUTH_DENIED://拒绝
                    result = R.string.wechat_login_error;
                    showToast(result);
                    finish();
                    return;
                default:
                    break;
            }
        } else {
            switch (resp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    result = R.string.errcode_success;
                /*if (AppManager.getAppManager().findActivity(SubjectActivity.class)) {*/
                    User user = UserProxy.getUser();
                    Map<String, String> map = new HashMap<>();
                    if (user != null) {
                        map.put("userId", user.getId());
                        map.put("token", user.getToken());
                    }
                    sendHttpRequest( API.AFTER_SHARE, map);
                    //}
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    result = R.string.errcode_cancel;
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    result = R.string.errcode_deny;
                    break;
                default:
                /*result = R.string.errcode_unknown;
                User user = WangYuApplication.getUser(this);
                Map<String, String> map = new HashMap<>();
                if (user != null) {
                    map.put("userId", user.getId());
                    map.put("token", user.getToken());
                }
                sendHttpPost(HttpConstant.SERVICE_HTTP_AREA + HttpConstant.AFTER_SHARE, map, HttpConstant.AFTER_SHARE);*/
                    result = R.string.errcode_failed;
                    break;
            }
            if (ShareToFriendsUtil.mSharePopWindow != null) {
                ShareToFriendsUtil.mSharePopWindow.dismiss();

                ShareToFriendsUtil.mSharePopWindow = null;
            }
            this.finish();
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(API.AFTER_SHARE)) {
            if (object.has("extend")) {
                try {
                    String extendStr = object.getString("extend");
                    showTaskCoins(extendStr);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


        } else if (method.equals(API.THIRD_LOGIN)) {
            toBindingOrtoMain(object);
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        showToast(errMsg);
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (method.equals(API.GET_USER_INFO_BY_WEIXIN_LOGIN)) {//因为微信登陆成功后去请求信息时返回值得字段里是没有code的，所以它请求成功后是在onfail里，而不是onsuccess
            thirdLogin(object);
        } else {
            showToast(object.toString());
        }
    }

    /**
     * 第三方登陆后绑定
     *
     * @param object
     */
    private void thirdLogin(JSONObject object) {
        try {
            if (object.has("openid")) {
                openid = object.getString("openid");
            }
            Map<String, String> map = new HashMap<>();
            map.put("openId", openid);
            map.put("platform", 2 + "");//1qq2微信3微博,必传
            sendHttpRequest( API.THIRD_LOGIN, map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
//                intent = new Intent(context, MobileLoginOrBindingActivity.class);
                intent.putExtra("platform", 2);//	1qq2微信3微博,必传
                intent.putExtra("openId", openid);
                intent.putExtra("type", 1);
                startActivity(intent);
                finish();
            } else {
                User user = new Gson().fromJson(object.getString("object").toString(), User.class);
                //WangYuApplication.getJpushUtil().setAlias(context.getResources().getString(R.string.alias) + user.getId());
//                WangYuApplication.getJpushUtil().setAliasWithTags(getResources().getString(R.string.alias) + user.getId(), JPushUtil.initTags(user));
                showToast("登录成功");
                UserProxy.setUser(user);
                setResult(RESULT_OK);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showTaskCoins(String extendStr) {
        if (!TextUtils.isEmpty(extendStr) && (!extendStr.equals("{}"))) {
            try {
                JSONObject jsonObj = new JSONObject(extendStr);
                ArrayList<CompleteTask> tasks = new Gson().fromJson(jsonObj.getString("completeTasks"),
                        new TypeToken<List<CompleteTask>>() {
                        }.getType());
                if (tasks != null) {
                    for (int i = 0; i < tasks.size(); i++) {
                        CompleteTask currentTask = tasks.get(i);
                        int type = currentTask.getTaskType();
                        int currentTaskIdentify = currentTask.getTaskIdentify();
                        if (type == 1) {
                            switch (currentTaskIdentify) {
                                case Constants.SHARE:
                                    showCoinToast("分享     +" + currentTask.getCoin() + "金币");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void showCoinToast(String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView imageCodeProject = new ImageView(context);
        imageCodeProject.setImageResource(R.drawable.coin_icon);
        toastView.addView(imageCodeProject, 0);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}