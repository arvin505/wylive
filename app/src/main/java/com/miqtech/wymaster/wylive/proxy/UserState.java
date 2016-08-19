package com.miqtech.wymaster.wylive.proxy;

import android.content.Context;
import android.content.Intent;

/**
 * Created by arvin on 2016/8/19.
 */
public interface UserState {

    /**
     * 用户行为-跳转
     *
     * @param context
     * @param intent
     */
    void jump(Context context, Intent intent);

    /**
     * 后续新增
     * 更多用户行为
     * 包括点赞，分享，收藏，等等
     */

}
