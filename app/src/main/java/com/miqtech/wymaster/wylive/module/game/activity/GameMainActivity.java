package com.miqtech.wymaster.wylive.module.game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.game.adapter.GameMainAdapter;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.module.live.LivePlayListActivity;
import com.miqtech.wymaster.wylive.module.live.LiveVideoListActivity;
import com.miqtech.wymaster.wylive.module.live.PlayVideoActivity;
import com.miqtech.wymaster.wylive.module.live.VideoListActivity;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.module.search.ui.adapter.VideoListAdapter;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;

import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by xiaoyi on 2016/8/25.
 * 游戏专区
 */
@LayoutId(R.layout.activity_gamemain)
public class GameMainActivity extends BaseAppCompatActivity implements AppBarLayout.OnOffsetChangedListener, RecycleViewItemClickListener {
    @BindView(R.id.img_poster)
    KenBurnsView imgPoster;
    @BindView(R.id.appbar)
    AppBarLayout appbar;

    @BindView(R.id.img_bar_follow)
    ImageView imgBarFollor;

    @BindView(R.id.ll_game_info)
    LinearLayout llGameInfo;

    @BindView(R.id.rvContent)
    RecyclerView rvContent;

    @BindView(R.id.tv_bar_title)
    TextView tvBarTitle;

    @BindView(R.id.img_game_icon)
    ImageView imgGameIcon;

    @BindView(R.id.tv_game_name)
    TextView tvGameName;
    @BindView(R.id.tv_live_count)
    TextView tvLiveCount;
    @BindView(R.id.tv_video_count)
    TextView tvVideoCount;

    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 20;

    private GridLayoutManager layoutManager;
    private GameMainAdapter mAdapter;

    private List<SearchWrapper<Object>> mData = new ArrayList<>();
    private String gameId;
    private int page = 1;
    private int pageSize = 12;
    private LiveTypeInfo mGame;

    @Override
    protected void initViews(Bundle savedInstanceState) {

        gameId = getIntent().getExtras().getString("gameId");
        loadGameData();
        appbar.addOnOffsetChangedListener(this);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mData.get(position).type == 1) {
                    return 1;
                }
                return 2;
            }
        });

        rvContent.setLayoutManager(layoutManager);


        mAdapter = new GameMainAdapter(mData, this);
        rvContent.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();

        int currentScrollPercentage = (Math.abs(verticalOffset)) * 100
                / mMaxScrollSize;

        llGameInfo.setAlpha((100 - currentScrollPercentage) / 100f);
        tvBarTitle.setAlpha(currentScrollPercentage / 100f);
    }

    @OnClick({R.id.img_bar_follow, R.id.img_bar_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_bar_follow:
                favorGame();
                break;
            case R.id.img_bar_share:
                showToast("分享");
                break;
        }
    }

    private void setHeadViews(LiveTypeInfo game) {
        AsyncImage.displayImage(game.getPoster(), imgPoster);
        AsyncImage.displayImage(game.getIcon(), imgGameIcon);
        tvBarTitle.setText(game.getName());
        tvGameName.setText(game.getName());
        tvLiveCount.setText(game.getLiveNum() + "");
        tvVideoCount.setText(game.getVideoNum() + "");
        imgBarFollor.setImageResource(game.getHasFavor() == 0 ? R.drawable.icon_follow_game : R.drawable.icon_followed_game);
    }

    private void generateData() {
        String liveTag = new String("推荐直播");
        SearchWrapper live = new SearchWrapper(0, liveTag);
        mData.add(live);


        for (int i = 0; i < 4; i++) {
            LiveInfo info = new LiveInfo();

            info.setIcon("http://img0.imgtn.bdimg.com/it/u=1373767599,2440349061&fm=206&gp=0.jpg");
            info.setGameName("雄霸天下" + i);
            info.setCommentNum(i * 1233 + 124);
            info.setOnlineNum(i % 584);
            info.setSex(i % 2);
            info.setNickname("花花爱吃鱼");
            info.setUserIcon("http://i.guancha.cn/news/2016/08/24/20160824174119510.jpg");
            info.setTitle("小黄人大战金馆长");
            mData.add(new SearchWrapper<Object>(1, info));

        }


        mData.add(new SearchWrapper<Object>(0, "精彩视频"));
        for (int i = 0; i < 4; i++) {
            LiveInfo info = new LiveInfo();
            info.setOnlineNum(23234);
            info.setIcon("http://img4.imgtn.bdimg.com/it/u=1223861339,559943435&fm=21&gp=0.jpg");
            info.setNickname("神农" + i);
            info.setTitle("直播尝百草");
            info.setGameName("雄霸天下" + i + "2");
            info.setCommentNum(i * 1233 + 124);
            info.setSex(i % 2);
            info.setPlayTimes(i % 3);
            mData.add(new SearchWrapper<Object>(2, info));
        }
    }


    @Override
    public void onItemClick(View view, int position) {
        SearchWrapper data = mData.get(position);
        if (data.type == 0) {   //标签  更多被点击
            if (data.data.equals("精彩视频")) {
                Bundle bundle = new Bundle();
                bundle.putString("gameId", gameId);
                jumpToActivity(VideoListActivity.class, bundle);
            } else if (data.data.equals("推荐直播")) {
                Bundle bundle = new Bundle();
                bundle.putString("gameId", mGame.getId());
                jumpToActivity(LivePlayListActivity.class, bundle);
            }
        } else {
            if (data.type == 1) {   //直播
                if (((LiveInfo) data.data).getType() == 1) {
                    Intent intent = new Intent(this, LiveRoomActivity.class);
                    intent.putExtra("id", ((LiveInfo) mData.get(position).data).getId() + "");
                    // intent.putExtra("rtmp", ((LiveInfo) mData.get(position).data).getRtmp());
                    jumpToActivity(intent);
                } else {
                    Intent intent = new Intent(this, PlayVideoActivity.class);
                    intent.putExtra("videoId", ((LiveInfo) mData.get(position).data).getId() + "");
                    intent.putExtra("rtmp", ((LiveInfo) mData.get(position).data).getRtmp());
                    jumpToActivity(intent);
                }


            } else if (data.type == 2) {  //视频
                Intent intent = new Intent(this, PlayVideoActivity.class);
                intent.putExtra("videoId", ((LiveInfo) mData.get(position).data).getId() + "");
                intent.putExtra("rtmp", ((LiveInfo) mData.get(position).data).getRtmp());
                jumpToActivity(intent);
            }
        }
    }

    private void loadGameData() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("gameId", gameId);
        User user = UserProxy.getUser();
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        } else {
            loadGameDataNoUser();
            return;
        }
        sendHttpRequest(API.GAME_SEPCIAL, params);
    }

    private void loadGameDataNoUser() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("gameId", gameId);
        sendHttpRequest(API.GAME_SEPCIAL, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            switch (method) {
                case API.GAME_SEPCIAL:
                    initData(object);
                    if (mData == null || mData.isEmpty()) {
                        showErrorPage("直播被怪兽抓走了，我们正在全力营救!", 0);
                    } else {
                        hideErrorPage();
                    }
                    break;
                case API.FAVOR_GAME:
                    int hasFavor = object.getJSONObject("object").getInt("has_subscribe");
                    imgBarFollor.setImageResource(hasFavor == 0 ? R.drawable.icon_follow_game : R.drawable.icon_followed_game);
                    Observerable.getInstance().notifyChange(ObserverableType.ATTENTION_GAME, mGame);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        try {
            if (object.getInt("code") == -1) loadGameDataNoUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initData(JSONObject object) throws Exception {
        Gson gson = new Gson();
        mGame = gson.fromJson(object.getJSONObject("object").
                getJSONObject("gameInfo").toString(), LiveTypeInfo.class);
        setHeadViews(mGame);
        List<LiveInfo> lives = gson.fromJson(object.getJSONObject("object").getJSONArray("liveList")
                .toString(), new TypeToken<List<LiveInfo>>() {
        }.getType());

        /*

        List<LiveInfo> videos = gson.fromJson(object.getJSONObject("object").getJSONObject("videoList")
                .getJSONArray("list").toString(), new TypeToken<List<LiveInfo>>() {
        }.getType());*/

        initListData(lives, Collections.<LiveInfo>emptyList());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 包装列表数据
     *
     * @param lives
     * @param videos
     */
    private void initListData(List<LiveInfo> lives, List<LiveInfo> videos) {

        /*if (lives != null && !lives.isEmpty()) {
            mData.add(new SearchWrapper<Object>(0, "推荐直播"));
        }*/
        for (int i = 0; i < 4 && i < lives.size(); i++) {
            mData.add(new SearchWrapper<Object>(1, lives.get(i)));
        }

        if (videos != null && !videos.isEmpty()) {
            mData.add(new SearchWrapper<Object>(0, "精彩视频"));
        }
        for (int i = 0; i < 4 && i < videos.size(); i++) {
            mData.add(new SearchWrapper<Object>(2, videos.get(i)));
        }
    }

    private void favorGame() {
        if (mGame == null) return;
        User user = UserProxy.getUser();

        if (user != null) {
            Map<String, String> params = new HashMap<>();
            params.put("userId", user.getId());
            params.put("token", user.getToken());
            params.put("gameId", mGame.getId());
            sendHttpRequest(API.FAVOR_GAME, params);
        } else {
            jumpToActivityForResult(LoginActivity.class, 1);
        }
    }
}
