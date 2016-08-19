package com.miqtech.wymaster.wylive.constants;

/**
 * Created by xiaoyi on 2016/8/15.
 */
public class API {

    public static String HOST = "http://172.16.2.62/";
    // public static String HOST = "http://wy.api.wangyuhudong.com/";
//    public static String HOST = "http://api.wangyuhudong.com/";

    public final static String IMAGE_HOST = "http://img.wangyuhudong.com/";


    /**
     * 注册
     */
    public final static String LOGIN = "login";

    /**
     * 直播游戏类型
     */
    public static final String GAMETYPE = "v4/live/gameType";
    /**
     * 直播通知
     */
    public static final String LIVE_NOTIFY = "v4/live/liveNotify";

    /**
     * 请求直播
     */
    public static final String LIVE_REQUEST = "v4/live/startLive";
}
