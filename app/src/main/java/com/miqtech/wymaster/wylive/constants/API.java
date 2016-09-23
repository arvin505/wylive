package com.miqtech.wymaster.wylive.constants;

/**
 * Created by xiaoyi on 2016/8/15.
 */
public class API {

//        public static String HOST = "http://172.16.2.62/";
    public static String HOST = "http://wy.api.wangyuhudong.com/";
//    public static String HOST = "http://api.wangyuhudong.com/";

    public final static String IMAGE_HOST = "http://img.wangyuhudong.com/";

    /**
     * 微信登陆成功后去获取微信的用户信息，比如openid、头像d等等
     */
    public static final String GET_USER_INFO_BY_WEIXIN_LOGIN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    /**
     * 第三方登陆后要传给后台的数据
     */
    public static final String THIRD_LOGIN = "thirdLogin?";

    /**
     * 第三方手机绑定
     */
    public static final String BIND_MOBILEPHONE = "bindMobilephone?";
    /**
     * 登陆
     */
    public final static String LOGIN = "login";

    /**
     * 发送验证码
     */
    public static final String SEND_SMS_CODE_MOBILE = "sendSMSCode?";

    /**
     * 校验验证码
     */
    public static final String CHECK_SMS_CODE = "/checkSMSCode?";

    /**
     * 图片验证码
     */
    public static final String IMAGE_CODE_REGISTER = "checkCode?phone=";
    /**
     * 注册
     */
    public static final String REGISTER = "register";
    /**
     * 找回密码
     */
    public static final String RESET_PASSWORD = "resetPassword";
    /**
     * 娱儿TV用户协议
     */
    public static final String YUER_AGREEMENT = "yuer/agreement";

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


    /**
     * 观看历史
     */
    public static final String BROWSE_HISTORY = "v4/live/browseHistory";


    /**
     * 关注列表
     */
    public static final String LIVE_SUBCRIBELIST = "tv/subcribe/subcribeList";


    /**
     * 直播大厅
     */
    public static final String LIVE_VIDEO_LIST = "v4/live/liveHall";
    /**
     * 首页banner图
     */
    public static final String HALL_BANNER = "tv/index/banner";

    /**
     * 分类列表
     */
    public static final String CATEGORY = "tv/sort/index";

    /**
     * 搜索
     */
    public static final String SEARCH = "tv/index/searchLive";


    /**
     * 直播间详情
     */
    public static final String LIVE_ROOM_DETAIL = "v4/live/liveDetail";

    /**
     * 离开直播间
     */
    public static final String LEAVE_ROOM = "v4/live/leaveLive";

    /**
     * 视频播放次数接口
     */
    public static final String PLAY_TIMES_REQUEST = "v4/live/countVideo";

    /**
     * 直播是否在线接口
     */
    public static final String LIVE_STATE = "v4/live/liveState";

    /**
     * 评论赛事资讯（官方赛、娱乐赛,资讯）
     */
    public static final String AMUSE_COMMENT_LIST = "v2/amuse/comment_list";

    /**
     * 删除娱乐赛事下的评论
     */
    public static final String DEL_COMMENT = "v2/amuse/del_comment";

    /**
     * 点赞评论v2
     */
    public static final String V2_COMMENT_PRAISE = "v2/comment/praise";

    /**
     * 评论娱乐赛
     */
    public static final String AMUSE_COMMENT = "v2/amuse/comment";

    /**
     * 订阅
     */
    public static final String LIVE_SUBSCRIBE = "v4/live/subscribe";

    /**
     * 直播列表
     */
    public static final String LIVE_LIST = "v4/live/liveList";

    /**
     * 视频列表
     */
    public static final String VIDEO_LIST = "v4/live/liveVideoList";

    /**
     * 视屏详情
     */
    public static final String VIDEO_DETAIL = "v4/live/liveVideoInfo";

    /**
     * 我的视频
     */
    public static final String MY_VIDEO = "tv/my/myVideo";

    /**
     * 观看历史
     */
    public static final String WATCH_HISTORY = "tv/my/browseHistory";

    /**
     * 我的
     */
    public static final String MINE = "tv/my/my";

    /**
     * 赛事（官方赛、娱乐赛）评论列表
     */
    public static final String V2_AMUSE_COMMENT_LIST = "v2/amuse/comment_list?";


    /**
     * 游戏专区
     */
    public static final String GAME_SEPCIAL = "tv/sort/section";

    /**
     * 关注游戏
     */
    public static final String FAVOR_GAME = "tv/sort/subscribeGame";

    /**
     * 提交评论
     */
    public static final String BOUNTY_UPGRADE = "v4/bounty/upGrade";

    /**
     * 图片上传接口(单图片)
     */
    public static final String UPLOAD_PIC = "common/upload?";

    // 删除照片
    public static final String DELETE_PHOTO = "my/deleteAlbum?";

    /**
     * 编辑资料
     */
    public static final String EDIT_INFO = "tv/my/editInfo?";

    /**
     * 邀请好友成功后回调接口；每日任务-分享
     */
    public static final String AFTER_SHARE = "malltask/shareTask?";
    /**
     * 直播分享
     */
    public static final String LIVE_SHARE = "v4/live/liveShare?id=";


    /**
     * 直播校验
     */
    public static final String LIVE_CHECK = "v4/live/androidModelCheck";

    /**
     * 退出直播
     */
    public static final String CLOSE_LIVE = "v4/live/closeLive";

    /**
     * 检查更新
     */
    public static final String CHECK_VERSION = "settings/version/client/android";

    /**
     * 清空播放历史
     */
    public static final String CLEAR_HISTORY = "tv/my/clearBrowseHistory";
}

