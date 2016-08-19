package com.miqtech.wymaster.wylive.proxy;

import android.content.Context;
import android.content.Intent;

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
}
