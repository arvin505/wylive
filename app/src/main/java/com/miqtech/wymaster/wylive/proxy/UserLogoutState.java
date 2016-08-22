package com.miqtech.wymaster.wylive.proxy;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.miqtech.wymaster.wylive.base.BaseFragment;

/**
 * Created by arvin on 2016/8/19.
 * 未登录状态
 */
public class UserLogoutState implements UserState {
    @Override
    public void jump(Context context, Intent intent) {
        // FIXME: 2016/8/19
        //跳转到登录界面
    }

    @Override
    public void getAttentionAnchor(BaseFragment fragment) {
        //// FIXME: 2016/8/22   跳转到登陆界面
    }
}
