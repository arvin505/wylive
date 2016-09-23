package com.miqtech.wymaster.wylive.module.live;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.FirstCommentDetail;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.LiveRoomAnchorInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.comment.PersonalCommentDetail;
import com.miqtech.wymaster.wylive.module.comment.SubmitGradesActivity;
import com.miqtech.wymaster.wylive.module.live.adapter.RecreationCommentAdapter;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.CustomMarqueeTextView;
import com.miqtech.wymaster.wylive.widget.LiveMediaController;
import com.miqtech.wymaster.wylive.widget.VerticalSeekBar;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshListView;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.PlayerCode;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by admin on 2016/8/4.
 */
@LayoutId(R.layout.activity_play_video_layout)
public class PlayVideoActivity extends BaseAppCompatActivity implements View.OnClickListener, RecreationCommentAdapter.ItemDataDealWith, PLMediaPlayer.OnCompletionListener,
        PLMediaPlayer.OnInfoListener,
        PLMediaPlayer.OnErrorListener,
        PLMediaPlayer.OnPreparedListener, PLMediaPlayer.OnVideoSizeChangedListener, LiveMediaController.ScreenChange {
    @BindView(R.id.rlSurfaceView)
    RelativeLayout rlSurfaceView; //整个播放布局
    @BindView(R.id.videoView)
    PLVideoTextureView videoView;//播放View
    @BindView(R.id.llBufferingIndicator)
    View llBufferingIndicator;//缓冲指示器
    @BindView(R.id.ivBack)
    ImageView ivBack;//返回按钮
    @BindView(R.id.tvTitle)
    CustomMarqueeTextView tvTitle;//视频标题
    @BindView(R.id.llComment)
    LinearLayout llComment; //评论按钮
    @BindView(R.id.lvLivePlayComments)
    PullToRefreshListView lvLivePlayComments;//评论页面
    @BindView(R.id.tvErrorPage)
    TextView tvErrorPage;// 错误页面
    @BindView(R.id.playControlView)
    ViewStub playControlView;//播放器暂停开始 进度条控制界面
    @BindView(R.id.rlVolumeUpDown)
    RelativeLayout rlVolumeUpDown;  //全屏下的音量界面
    @BindView(R.id.sbVolumeProgress)
    VerticalSeekBar sbVolumeProgress; //音量垂直进度条
    @BindView(R.id.tvComment)
    TextView tvComment;//评论提示语
    private ListView listView;

    private String mVideoPath; // 播放地址
    private Runnable mVideoReconnect;
    private boolean mIsCompleted = false; //是否播放完成
    private static final int REQ_DELAY_MILLS = 3000;
    private int mReqDelayMills = REQ_DELAY_MILLS;
    private Context context;
    private float radio = 9f / 16f; //屏幕的宽高比
    private int screenWidth;
    private AVOptions mAVOptions;
    private boolean isFullScreen = false;
    private User user;
    private int replySize = 10;//   replySize	否	string	楼中楼回复数量（不传默认返回5条）
    private int page = 1;
    private int pageSize = 10;
    private List<FirstCommentDetail> comments = new ArrayList<FirstCommentDetail>();
    private String replyListPosition;
    private int listId;
    private Dialog mDialog;
    private RecreationCommentAdapter adapter;
    private final int ISREFER = 1;//startActivityForResult(intent,)
    private final int type = 8;//评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1） 8视屏评论
    private String message; //输入框里面的消息
    private View headerView;
    private TextView tvAttention;
    private CircleImageView anchorHeader; //主播头像
    private TextView anchorTitle;  //标题
    private AudioManager audioManager;
    private int currentVolume; //当前音量
    private int maxVolume;  //最大音量
    private LiveMediaController mMediaController;
    private boolean isStop = false;
    private long currentPosition;
    private String videoId;  //视频的id
    private LiveRoomAnchorInfo videoInfo;//视频详情信息
    private static final int COMMENT_REQUEST = 3;
    private String imgName;//后台返回的图片名
    private ViewGroup.LayoutParams videoViewLayoutParams;
    private ImageView anchorSex ;//主播性别
    private TextView fansNum ;//粉丝数量
    private static final int MSG_PLAY_TIMES_REQUEST=101;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_PLAY_TIMES_REQUEST:
                    if (totalTime >=60  && !mIsCompleted) {
                        //倒计时时间
                        //设置倒计时时间
                        countDownRequest();

                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                            totalTime=0;
                        }
                    }
                    break;
            }
        }
    };
    @Override
    protected void initViews(Bundle savedInstanceState) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        context = this;
        // 初始化视频大小
        initPlayerView(true);
        initView();
        initData();
        initPlayParameter();
        setOnClickListener();
    }

    /**
     * 初始化播放器大小
     *
     * @param parentView
     */
    /**
     * 初始化播放器大小
     * 宽高比4：3
     */
    private void initPlayerView(boolean islandscape) {
        int videoHeight = (int) (screenWidth * radio);
        ViewGroup.LayoutParams lp = rlSurfaceView.getLayoutParams();
        videoViewLayoutParams = videoView.getLayoutParams();
        if (islandscape) {
            videoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        } else {
            videoViewLayoutParams.width = videoHeight * 9 / 16;
        }
        videoViewLayoutParams.height = videoHeight;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = videoHeight;
        rlSurfaceView.setLayoutParams(lp);
        videoView.setLayoutParams(videoViewLayoutParams);
    }

    /**
     * 初始化View
     */
    protected void initView() {
        //TODO 获取播放地址
        videoId = getIntent().getStringExtra("videoId");
        mVideoPath = getIntent().getStringExtra("rtmp");
        L.e(TAG, "播放地址" + mVideoPath);
        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction) && intentAction.equals(Intent.ACTION_VIEW)) {
            mVideoPath = intent.getDataString();
        }
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mMediaController = new LiveMediaController(this, playControlView);
        mMediaController.setPlayOriention(0);
        mMediaController.setScreenOrientation(false); //设置非全屏

        videoView.setMediaController(mMediaController);
        //  videoView.setMediaBufferingIndicator(llBufferingIndicator);
        //TODO 设置触摸控制音量和进度事件

        headerView = getLayoutInflater().inflate(R.layout.layout_info_header, null);
        listView=lvLivePlayComments.getRefreshableView();
        lvLivePlayComments.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listView.addHeaderView(headerView);
        initHeadView();

        adapter = new RecreationCommentAdapter(context, comments);
        listView.setAdapter(adapter);
        adapter.setReport(this);
        llBufferingIndicator.setVisibility(View.VISIBLE);
        tvComment.setText(getString(R.string.live_comment_hint,0+""));//设置默认评论数量为0
    }
    private void initHeadView() {
        anchorHeader = (CircleImageView) headerView.findViewById(R.id.anchorHeader);
        anchorTitle = (TextView) headerView.findViewById(R.id.anchorTitle);
        tvAttention = (TextView) headerView.findViewById(R.id.tvAttention);
        anchorSex=(ImageView)headerView.findViewById(R.id.anchorSex);
        fansNum = (TextView) headerView.findViewById(R.id.fansNum);
        tvAttention.setOnClickListener(this);
    }

    /**
     * 设置主播数据
     *
     * @param info
     */
    private void setData(LiveRoomAnchorInfo info) {
        if (info != null) {
            mVideoPath = info.getRtmp();
            L.e(TAG, "播放地址33333：：：" + mVideoPath);
            //开始播放
            videoView.setVideoPath(mVideoPath);
            videoView.requestFocus();
            videoView.start();
            AsyncImage.displayImage(info.getUserIcon(), anchorHeader, R.drawable.default_head);
            anchorTitle.setText(info.getNickname());
            setSubscribeState(info.getIsSubscibe() == 1 ? true : false);
            tvTitle.setText(info.getTitle());
            if(info.getSource()==0 && info.getScreen() == 0) {
                videoView.setDisplayOrientation(90);
            }
            initPlayerView(info.getScreen()==0?true:false);
            Utils.setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(), 10000, "W")), 3, getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(), 10000, "W")).length(), fansNum,R.color.bar_text_selected);
            anchorSex.setVisibility(View.VISIBLE);
            if (info.getSex() == 0) {
                anchorSex.setImageResource(R.drawable.live_play_men);
            } else {
                anchorSex.setImageResource(R.drawable.live_play_femen);
            }
            if (info.getScreen() == 0) {
                mMediaController.setPlayOriention(0);
            } else {
                mMediaController.setPlayOriention(1);
            }
        }
    }

    public void setSubscribeState(boolean isSubscribe) {
        if (isSubscribe) {
            tvAttention.setText(getResources().getString(R.string.live_room_attentioned));
            tvAttention.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            tvAttention.setTextColor(this.getResources().getColor(R.color.search_edit_border));
            Utils.setShapeBackground(tvAttention,0,0,R.color.anchor_state_offline, DeviceUtils.dp2px(this,5));
        } else {
            tvAttention.setText(getResources().getString(R.string.live_room_attention));
            tvAttention.setTextColor(this.getResources().getColor(R.color.white));
            tvAttention.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_attention,0,0,0);
            Utils.setShapeBackground(tvAttention,0,0,R.color.bar_text_selected, DeviceUtils.dp2px(this,5));

        }
    }
    protected void initData() {
        getVideoData();
        loadOfficalCommentList();
    }

    private void getVideoData() {
        //  showLoading();
        Map<String, String> params = new HashMap();
        params.put("videoId", videoId);
        if (UserProxy.getUser() != null) {
            params.put("userId", UserProxy.getUser().getId() + "");
            params.put("token", UserProxy.getUser().getToken() + "");
        }
        sendHttpRequest(API.VIDEO_DETAIL, params);
    }

    private void loadOfficalCommentList() {
        user = UserProxy.getUser();
        HashMap params = new HashMap();
        //TODO  战时写死
        params.put("amuseId", videoId);
        params.put("page", page + "");
        params.put("type", 8 + "");//	评论类型：1-娱乐赛评论；2-官方赛事评论 4自发赛评论。（不传默认为1）
        params.put("replySize", replySize + "");
        if (user != null) {
            params.put("userId", user.getId() + "");
            params.put("token", user.getToken() + "");
        }
        sendHttpRequest(API.AMUSE_COMMENT_LIST, params);
    }
    private void countDownRequest(){
        L.e(TAG,"countDownRequest 倒计时");
        Map<String, String> params = new HashMap();
        params.put("id",videoId);
        sendHttpRequest(API.PLAY_TIMES_REQUEST,params);
        hideLoading();
    }
    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        lvLivePlayComments.onRefreshComplete();
        L.e(TAG, "onSuccess" + object.toString());
        //  hideLoading();
        tvErrorPage.setVisibility(View.GONE);
        try {
            if (object == null) {
                return;
            }
            if (method.equals(API.AMUSE_COMMENT_LIST)) {
                initRecreationComment(object);
            } else if (method.equals(API.DEL_COMMENT)) {
                if (0 == object.getInt("code") && "success".equals(object.getString("result"))) {
                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    L.e("Delect", "删除成功" + replyListPosition);
                    if (!TextUtils.isEmpty(replyListPosition)) {
                        int idd = Integer.parseInt(replyListPosition);
                        int replycount = comments.get(listId).getReplyCount();
                        if (replycount > 1) {
                            comments.get(listId).setReplyCount(replycount - 1);
                        }
                        comments.get(listId).getReplyList().remove(idd);
                        replyListPosition = "";
                    } else {
                        comments.remove(listId);
                    }
                    adapter.notifyDataSetChanged();
                    showToast("删除成功");
                }
            } else if (method.equals(API.V2_COMMENT_PRAISE)) {
                int praisrNum;
                //  BroadcastController.sendUserChangeBroadcase(context);
                if (comments.get(listId).getIsPraise() == 0) {
                    praisrNum = comments.get(listId).getLikeCount();
                    comments.get(listId).setIsPraise(1);
                    comments.get(listId).setLikeCount(praisrNum + 1);
                } else if (comments.get(listId).getIsPraise() == 1) {
                    praisrNum = comments.get(listId).getLikeCount();
                    comments.get(listId).setIsPraise(0);
                    comments.get(listId).setLikeCount(praisrNum - 1);
                }
                adapter.notifyDataSetChanged();
            } else if (method.equals(API.AMUSE_COMMENT)) {//提交评论
                page = 1;
                pageSize = 10;
                loadOfficalCommentList();
                showToast("发表成功");
            } else if (method.equals(API.VIDEO_DETAIL)) {
                try {
                    if (object.getInt("code") == 0 && object.has("object")) {
                        Gson gs = new Gson();
                        videoInfo = gs.fromJson(object.getJSONObject("object").toString(), LiveRoomAnchorInfo.class);
                        setData(videoInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (method.equals(API.LIVE_SUBSCRIBE)) {
                try {
                    if (object.getInt("code") == 0 && "success".equals(object.getString("result"))) {
                        videoInfo.setIsSubscibe(videoInfo.getIsSubscibe() == 1 ? 0 : 1);
                        setSubscribeState(videoInfo.getIsSubscibe() == 1 ? true : false);
                        setSubscribeNum();
                        AnchorInfo anchorInfo = new AnchorInfo();
                        anchorInfo.setState(0);
                        anchorInfo.setFans(videoInfo.getFans());
                        anchorInfo.setRoom(videoInfo.getRoom());
                        anchorInfo.setName(videoInfo.getNickname());
                        anchorInfo.setId(videoInfo.getId());
                        anchorInfo.setIcon(videoInfo.getIcon());
                        Observerable.getInstance().notifyChange(ObserverableType.ATTENTION_ANCHOR,anchorInfo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else if(method.equals(API.PLAY_TIMES_REQUEST)){
                try {
                    if("0".equals(object.getString("code")) && "success".equals(object.getString("result"))){
                        //TODO 发送计算播放次数成功  更新播放次数
                        L.e(TAG,"播放成功");
                        Observerable.getInstance().notifyChange(ObserverableType.VIDEO_TIMES,Integer.parseInt(videoId));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        //  hideLoading();
        lvLivePlayComments.onRefreshComplete();
        try {
            int code = object.getInt("code");
            String result = object.getString("result");
            showToast(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        lvLivePlayComments.onRefreshComplete();
        //      hideLoading();
    }
   private void setSubscribeNum(){
       if(videoInfo.getIsSubscibe()==1) {
           Utils.setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(videoInfo.getFans()+1, 10000, "W")), 3,
                   getString(R.string.live_play_fans_num, Utils.calculate(videoInfo.getFans()+1, 10000, "W")).length(), fansNum, R.color.bar_text_selected);
           videoInfo.setFans(videoInfo.getFans()+1);
       }else{
           if(videoInfo.getFans()!=0){
               Utils.setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(videoInfo.getFans()-1, 10000, "W")), 3,
                       getString(R.string.live_play_fans_num, Utils.calculate(videoInfo.getFans()-1, 10000, "W")).length(), fansNum, R.color.bar_text_selected);
               videoInfo.setFans(videoInfo.getFans()-1);
           }
       }
   }
    private void initRecreationComment(JSONObject object) {
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                String strObj = object.getString("object");
                int totalComments=object.getJSONObject("object").getInt("total");
                tvComment.setText(getString(R.string.live_comment_hint,totalComments+""));
                JSONObject jsonObj = new JSONObject(strObj);
                if (jsonObj.has("list")) {
                    Gson gs = new Gson();
                    List<FirstCommentDetail> newComments = gs.fromJson(object.getJSONObject("object").getJSONArray("list").toString(), new TypeToken<List<FirstCommentDetail>>() {
                    }.getType());
                    comments.clear();
                    if (newComments != null && !newComments.isEmpty()) {
                        comments.addAll(newComments);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    comments.clear();
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initPlayParameter() {
        mAVOptions = new AVOptions();
        mAVOptions.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);//连接时间
        mAVOptions.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000); //读取视频流超时时间
        mAVOptions.setInteger(AVOptions.KEY_MEDIACODEC, 0); //1 硬解 0 软解
        mAVOptions.setInteger(AVOptions.KEY_START_ON_PREPARED, 1); //1 自动播放 0 不自动播放
        videoView.setAVOptions(mAVOptions);
        audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        L.e(TAG, "initPlayParameter:::" + mVideoPath);

}

    private void changeLandscape() {
        if (videoInfo.getScreen() == 0) {
            Log.i("changeLandscape", "变换横屏");
            rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 全屏
            isFullScreen = true;
            mMediaController.setScreenOrientation(true);
            ((RelativeLayout.LayoutParams)videoView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.requestLayout();
        } else {
            rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            isFullScreen = true;
            mMediaController.setScreenOrientation(true);
            ((RelativeLayout.LayoutParams)videoView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
            videoView.requestLayout();
            Log.i("changeLandscape", "222222变换横屏");
        }
        llComment.setVisibility(View.GONE);
        mTintManager.setStatusBarTintEnabled(false);
    }

    private void backLandscape() {
        Log.i("changeLandscape", "变换竖屏");
        //影藏锁屏图标
        if (isFullScreen) {
            if (videoInfo.getScreen() == 0) {
                int screenWidth = getWindowManager().getDefaultDisplay().getHeight();
                int videoHeight = (int) (screenWidth * radio);
                rlSurfaceView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, videoHeight));
                videoView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        videoHeight));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 还原
                ((RelativeLayout.LayoutParams)videoView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
                isFullScreen = false;
                mMediaController.setScreenOrientation(false);
            } else {
                int videoHeight = (int) (screenWidth * radio);
                ViewGroup.LayoutParams lp = rlSurfaceView.getLayoutParams();
                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                lp.height = videoHeight;
                L.e(TAG, "initPlayerView" + "宽度" + lp.width + ":::" + lp.height);
                rlSurfaceView.setLayoutParams(lp);
                videoView.setLayoutParams(videoViewLayoutParams);
                ((RelativeLayout.LayoutParams)videoView.getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
                L.e(TAG, "22222222宽" + videoViewLayoutParams.width + ":::" + videoViewLayoutParams.height);
                isFullScreen = false;
                mMediaController.setScreenOrientation(false);
            }
            mTintManager.setStatusBarTintEnabled(true);
            llComment.setVisibility(View.VISIBLE);
        }
    }

    /**
     * View设置点击事件
     */
    private void setOnClickListener() {
        videoView.setOnErrorListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnInfoListener(this);
        videoView.setOnPreparedListener(this);
        ivBack.setOnClickListener(this);
        llComment.setOnClickListener(this);
        rlSurfaceView.setOnTouchListener(onTouchListener);
        lvLivePlayComments.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                loadOfficalCommentList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //   if(!videoView.isPlaying() && isStop){
        L.e(TAG, "当前位置  onStop" + currentPosition + ":::" + videoView.isPlaying());
        videoView.start();
        videoView.seekTo(currentPosition);
        isStop = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(null);
        mMediaController = null;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivFullScreenBtn:
                if (isFullScreen) {
                    backLandscape();
                } else {
                    changeLandscape();
                }
                break;
            case R.id.ivBack:
                if (isFullScreen) {
                    backLandscape();
                } else {
                    mIsCompleted=true;
                    clearResource();
                    onBackPressed();
                }
                break;
            case R.id.llComment:
                Intent intent=new Intent(this, SubmitGradesActivity.class);
                intent.putExtra("fromType",1);
                startActivityForResult(intent,COMMENT_REQUEST);
                break;
            case R.id.tvAttention:
                getAttentionRequest();
                break;
        }
    }

    /**
     * 离开播放间释放资源
     */
    private void clearResource() {
        videoView.stopPlayback();
        if (mMediaController != null) {
            mMediaController.clearResouce();
        }
    }

    private void getAttentionRequest() {
        //     showLoading();
        Map<String, String> params = new HashMap();
        if (UserProxy.getUser() != null && videoInfo!=null) {
            params.put("userId", UserProxy.getUser().getId() + "");
            params.put("token", UserProxy.getUser().getToken());
            params.put("upUserId", videoInfo.getUpUserId() + "");
            sendHttpRequest(API.LIVE_SUBSCRIBE, params);
        } else {
            jumpToActivity(LoginActivity.class);
        }

    }

    /**
     * 提交评论
     */
    private void submitComment() {
        Map<String, String> map = new HashMap();
        user = UserProxy.getUser();
        if (user != null) {
            map.put("amuseId", videoId + "");
            map.put("userId", user.getId() + "");
            map.put("token", user.getToken() + "");
            map.put("content", Utils.replaceBlank(message));
            if (!TextUtils.isEmpty(imgName)) {
                map.put("img", imgName);
            }
            map.put("type", 8 + "");// type否	string	评论类型：1-娱乐赛评论；2-官方赛事评论。（不传默认为1）
            sendHttpRequest(API.AMUSE_COMMENT, map);
        } else {
            jumpToActivity(LoginActivity.class);
        }
    }

    @Override
    public void delect(int position) {
        if (!comments.isEmpty() && position < comments.size()) {
            listId = position;
            creatDialogForDelect(comments.get(position).getId());
            Log.i(TAG, "删除的用户的id" + comments.get(position).getUserId());
        }
    }

    @Override
    public void praiseComment(int position) {
        user = UserProxy.getUser();
        if (user != null) {
            listId = position;
            Map<String, String> map = new HashMap<>();
            map.put("commentId", comments.get(position).getId() + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpRequest(API.V2_COMMENT_PRAISE, map);
        } else {
            jumpToActivity(LoginActivity.class);
            //  showToast(getResources().getString(R.string.pleaseLogin));
        }
    }

    @Override
    public void replyComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", videoId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 1);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookComment(int position) {
        Intent intent = new Intent(context, PersonalCommentDetail.class);
        intent.putExtra("amuseId", videoId);
        intent.putExtra("type", type);
        intent.putExtra("parentId", comments.get(position).getId());
        intent.putExtra("isPopupKeyboard", 0);//判断是否弹出键盘   1  弹出  0不弹出   默认不弹
        startActivityForResult(intent, ISREFER);
    }

    @Override
    public void lookCommentSection() {
        skipCommentSection();
    }

    @Override
    public void delectReplyReply(int position, int replyListid) {
        user = UserProxy.getUser();

        if (user != null) {
            if (!comments.isEmpty() && !comments.get(position).getReplyList().isEmpty()) {
                listId = position;
                replyListPosition = replyListid + "";
                creatDialogForDelect(comments.get(position).getReplyList().get(replyListid).getId());
            }
        } else {
            jumpToActivity(LoginActivity.class);
        }
    }

    /**
     * 跳到评论区页面
     */
    public void skipCommentSection() {
//        Intent intent = new Intent(context, CommentsSectionActivity.class);
//        intent.putExtra("amuseId", videoId + "");
//        intent.putExtra("type", type);
//        startActivityForResult(intent, ISREFER);
    }

    private void creatDialogForDelect(final String id) {
        mDialog = new Dialog(context, R.style.register_style);
        mDialog.setContentView(R.layout.dialog_register_marked_words);
        TextView title = (TextView) mDialog.findViewById(R.id.dialog_title_register);
        TextView yes = (TextView) mDialog.findViewById(R.id.dialog_register_yes_pact);
        TextView no = (TextView) mDialog.findViewById(R.id.dialog_register_no_pact);
        View line = mDialog.findViewById(R.id.dialog_line_no_pact);
        line.setVisibility(View.VISIBLE);
        no.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        title.setText("是否删除评论");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delectcomment(id);
                L.e("Delect", "删除" + id);
                mDialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void delectcomment(String id) {
        user = UserProxy.getUser();
        if (user != null) {
            Map<String, String> map = new HashMap<>();
            map.put("commentId", id + "");
            map.put("userId", user.getId());
            map.put("token", user.getToken());
            sendHttpRequest(API.DEL_COMMENT, map);
            L.e("Delect", "删除q赢球" + id + "::" + user.getId() + ":::" + user.getToken());
        } else {
            jumpToActivity(LoginActivity.class);
            //   showToast(getResources().getString(R.string.pleaseLogin));
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISREFER && data != null) {
            int myIsRefre = data.getIntExtra("isRefre", -1);
            if (myIsRefre == 1) {
                loadOfficalCommentList();
            }
        } else if(requestCode==COMMENT_REQUEST && resultCode==SubmitGradesActivity.RESULT_OK && data!=null){
            imgName =data.getStringExtra("imgName");
            message=data.getStringExtra("remark");
            submitComment();
        }
    }

    @Override
    public void changeToLandscape() {
        changeLandscape();
    }

    @Override
    public void changeToPortrait() {
        backLandscape();
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {

        float startX;
        float startY;
        long toTime; // 播放的进度位置
        long startTime = 0;
        // 滑动全长的时间
        // 根据窗口（surfaceView）大小，计算出的横向滑动时毫秒/像素
        float secondsPerPixel;

        // 滑动时确定方向的临界值
        final static float check = 30;

        // 横向滑动整屏跳转的时间
        public final static int PROGRESS_VER_SCREEN = 3 * 60 * 1000;

        // 每像素等于多少音量
        /** 是否是调节音量 */
        boolean isChangeVolume = false;
        int startVolume;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //全屏  且 锁屏 没有触摸事件   非全屏没有触摸事件
            //TODO 锁屏判断
            Log.i("onTouch", "onTouch2222222" + isFullScreen);
            if (!isFullScreen) {
                return true;
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (isChangeVolume) {
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    return true;
                }
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                secondsPerPixel = (float) PROGRESS_VER_SCREEN
                        / (float) v.getWidth(); // 166.66667 ???
                startX = event.getX();
                startY = event.getY();

                startTime = videoView.getCurrentPosition();
                sbVolumeProgress.setProgress(currentVolume);   //设置当前音量
                toTime = startTime;
                isChangeVolume = false;
                startVolume = currentVolume; // 0??
                Log.i("onTouch", "onTouch" + secondsPerPixel + ":::" + startX + "::" + startY + ":::" + startTime + "::" + currentVolume);
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                Log.i("onTouch", " event.getAction() == MotionEvent.ACTION_MOVE");
                float endX = event.getX();
                float endY = event.getY();

                // 计算出横纵两方向的偏离值
                double moveX = (endX - startX);
                double moveY = (endY - startY);

                // 横纵方向的偏离值谁先到达临界值，方向便是谁
                if ((!isChangeVolume)) {
                    if (Math.abs(moveX) > check) {
                        //TODO 调节进度条
                        Log.i("onTouch", " isChangeProgress = true;");
                    } else if (Math.abs(moveY) > check) {
                        //如果需要控制亮度  就打开此处
                        isChangeVolume = true;
                        Log.i("onTouch", " isChangeVolume = true;");
                    }
                } else {
                    if (isChangeVolume) {
                        showVolume();
                        float addVolume = (startY - endY) / v.getHeight()
                                * maxVolume * 4f;
                        int toVolume = (int) (startVolume + addVolume);
                        if (toVolume > maxVolume) {
                            toVolume = maxVolume;
                        } else if (toVolume < 0) {
                            toVolume = 0;
                        }
                        sbVolumeProgress.setProgress(toVolume);
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                toVolume, 0);
                    }
                }

            }
            return true;
        }
    };

    private void showVolume() {
        if (null != rlVolumeUpDown && isFullScreen) {
            rlVolumeUpDown.setVisibility(View.VISIBLE);
        }
        hideVolumeAfterMillis();   //2秒之后音量键消失
    }

    protected void hideVolumeAfterMillis() {
        rlVolumeUpDown.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != rlVolumeUpDown && rlVolumeUpDown.getVisibility() == View.VISIBLE) {
                    rlVolumeUpDown.setVisibility(View.GONE);
                }
            }
        }, 2000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // 取消长按menu键弹出键盘事�?
                if (event.getRepeatCount() > 0) {
                    return true;
                }
                if (isFullScreen) {
                    backLandscape();
                    if (mMediaController != null) {
                        mMediaController.changeFullScreenPic();
                    }
                } else {
                    mIsCompleted=true;
                    clearResource();
                    onBackPressed();
                    return true;
                }
            case KeyEvent.KEYCODE_HOME:
                //TODO home键
                return true;
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
        L.e(TAG, "当前位置  onPause" + currentPosition);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        isStop = true;
        L.e(TAG, "当前位置  onStop" + currentPosition);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        llBufferingIndicator.setVisibility(View.GONE);
        mIsCompleted = true;
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int extra) {
        L.e(TAG, "extra" + extra);
        if (extra == PlayerCode.EXTRA_CODE_INVALID_URI || extra == PlayerCode.EXTRA_CODE_EOF) {
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
            return true;
        }
        //播放完成  并且没有播放列表  重新播放
        if (mIsCompleted && extra == PlayerCode.EXTRA_CODE_EMPTY_PLAYLIST) {
            Log.d(TAG, "mVideoView reconnect!!!");
            videoView.removeCallbacks(mVideoReconnect);
            mVideoReconnect = new Runnable() {
                @Override
                public void run() {
                    videoView.setVideoPath(mVideoPath);
                }
            };
            videoView.postDelayed(mVideoReconnect, mReqDelayMills);
            mReqDelayMills += 200;
        } else if (extra == PlayerCode.EXTRA_CODE_404_NOT_FOUND) {
            // NO ts exist
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
        } else if (extra == PlayerCode.EXTRA_CODE_IO_ERROR) {
            // NO rtmp stream exist
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
        if (what == PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_VIDEO_RENDERING_START)");
            if (mMediaController != null) {
                mMediaController.setControlView(true);
            }
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
        } else if (what == PLMediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.VISIBLE);
        } else if (what == PLMediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.i(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
            if (llBufferingIndicator != null)
                llBufferingIndicator.setVisibility(View.GONE);
        } else if (what == PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START) {
            Log.i(TAG, "duration:" + videoView.getDuration());
        } else if (what == IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.i(TAG, "duration:" + videoView.getDuration());
        }
        return true;
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {
        videoView.requestLayout();
        llBufferingIndicator.setVisibility(View.GONE);
        mReqDelayMills = REQ_DELAY_MILLS;
        mMediaController.show(3000);
        mIsCompleted = false;
        timeOut();
    }

    @Override
    public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1) {

    }

    private MyTimerTask task;
    private Timer timer;
    private int totalTime = 0;
    private void timeOut() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new MyTimerTask();
        }
        timer.schedule(task, 1000, 1000);
    }
    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            totalTime++;
            handler.sendEmptyMessage(MSG_PLAY_TIMES_REQUEST);
            L.e(TAG, "On Prepared ! 倒计时开始");
        }
    }
}
