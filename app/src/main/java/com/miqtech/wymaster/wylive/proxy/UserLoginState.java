package com.miqtech.wymaster.wylive.proxy;

import android.content.Context;
import android.content.Intent;

import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.User;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by arvin on 2016/8/19.
 * 已登录状态，
 */
public class UserLoginState implements UserState {
    @Override
    public void jump(Context context, Intent intent) {
        // FIXME: 2016/8/19
        //
        context.startActivity(intent);
    }

    @Override
    public void getAttentionAnchor(BaseFragment fragment) {
        User user = UserProxy.getUser();
        Map<String, String> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        fragment.sendHttpRequest(API.BROWSE_HISTORY, params);
    }
}
