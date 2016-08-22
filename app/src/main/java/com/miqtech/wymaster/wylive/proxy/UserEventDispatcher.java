package com.miqtech.wymaster.wylive.proxy;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.miqtech.wymaster.wylive.base.BaseFragment;

/**
 * Created by arvin on 2016/8/19.
 * 用户行为分发
 * 分享，点赞，跳转到某些界面都是需要判断用户的登录状态的
 * 传统方式需要使用大量的if else 代码
 * 采用状态模式，当用户处于不同状态的时候，采取不同的行为模式
 * （即：用户登录了，让其执行这些动作，没有登录，跳转到登录界面）
 * UserEventDispatcher中的mUserState与UserProxy耦合 保持联动，这样的设计是合理的
 * 用户不为空的时候，即判定其状态是登录状态，否则为登出状态
 */
public class UserEventDispatcher {
    static UserState mUserState = new UserLogoutState();

    public static void setUserState(UserState mUserState) {
        UserEventDispatcher.mUserState = mUserState;
    }

    /**
     * @param context 执行用户行为的上下文
     * @param intent  跳转intent
     */
    public static void jump(Context context, Intent intent) {
        mUserState.jump(context, intent);
    }

    public static void getAttentionAnchor(BaseFragment fragment){
        mUserState.getAttentionAnchor(fragment);
    }
}
