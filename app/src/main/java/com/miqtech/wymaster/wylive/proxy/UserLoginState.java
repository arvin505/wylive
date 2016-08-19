package com.miqtech.wymaster.wylive.proxy;

import android.content.Context;
import android.content.Intent;

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
}
