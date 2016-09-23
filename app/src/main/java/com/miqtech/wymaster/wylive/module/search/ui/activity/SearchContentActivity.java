package com.miqtech.wymaster.wylive.module.search.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.module.game.activity.GameMainActivity;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.live.PlayVideoActivity;
import com.miqtech.wymaster.wylive.module.search.ui.adapter.SearchContentAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/23.
 */
@LayoutId(R.layout.activity_search_content)
@Title(title = "搜索结果")
public class SearchContentActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    String key;
    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;

    List<SearchWrapper<Object>> mResult = new ArrayList<>();
    GridLayoutManager layoutManager;
    SearchContentAdapter mAdapter;

    private int page = 1;
    private int pageSize = 1;
    private String TYPE = "1";
    private List<LiveTypeInfo> games;
    private List<LiveInfo> lives;
    private List<AnchorInfo> anchors;
    private List<LiveInfo> videos ;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        key = getIntent().getExtras().getString("key");
        search(key);
        mAdapter = new SearchContentAdapter(this, mResult);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mResult.get(position).type == 2) {
                    return 1;
                }
                return 2;
            }
        });
        rvSearch.setLayoutManager(layoutManager);
        rvSearch.setAdapter(mAdapter);
        rvSearch.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter.setOnItemClickListener(this);

    }

    private void generateData() {
        mResult = new ArrayList<>();
        String tag = new String("相关游戏");
        SearchWrapper gameTag = new SearchWrapper(0, tag);
        mResult.add(gameTag);
        LiveTypeInfo gameInfo = new LiveTypeInfo();
        gameInfo.setName("王者荣耀");
        gameInfo.setIcon("http://img2.gtimg.com//dbimg/2013/mainpiclib/20130808/16/bfff5b3eabf813d2952d274919b641422.jpg");
        gameInfo.setVideoNum(1513);
        gameInfo.setLiveNum(4815);
        SearchWrapper game = new SearchWrapper(1, gameInfo);
        mResult.add(game);

        String liveTag = new String("相关直播");
        SearchWrapper live = new SearchWrapper(0, liveTag);
        mResult.add(live);


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
            mResult.add(new SearchWrapper<Object>(2, info));

        }

        String anchorTAG = new String("相关主播");
        SearchWrapper anchor = new SearchWrapper(0, anchorTAG);
        mResult.add(anchor);

        for (int i = 0; i < 4; i++) {
            AnchorInfo info = new AnchorInfo();
            info.setFans(23234);
            info.setIcon("http://img2.a0bi.com/upload/ttq/20160709/1468043314019.png");
            info.setName("这主播有毒" + i);
            info.setRoom(i * 1233 + 124 + "");
            info.setState(i % 3);
            mResult.add(new SearchWrapper<Object>(3, info));

        }
        mResult.add(new SearchWrapper<Object>(0, "相关视频"));
        for (int i = 0; i < 4; i++) {
            LiveInfo info = new LiveInfo();
            info.setOnlineNum(23234);
            info.setIcon("http://img4.imgtn.bdimg.com/it/u=1223861339,559943435&fm=21&gp=0.jpg");
            info.setNickname("神农" + i);
            info.setTitle("直播尝百草");
            info.setCommentNum(i * 1233 + 124);
            info.setSex(i % 2);
            info.setPlayTimes(i % 3);
            mResult.add(new SearchWrapper<Object>(4, info));
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        SearchWrapper wrapper = mResult.get(position);

        switch (wrapper.type) {
            case 0:   //更多标签
                if (wrapper.data.equals("相关主播")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    jumpToActivity(SearchAnchorActivity.class, bundle);
                } else if (wrapper.data.equals("相关直播")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    jumpToActivity(SearchVideoActivity.class, bundle);
                } else if (wrapper.data.equals("相关视频")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    jumpToActivity(SearchVideoActivity.class, bundle);
                }
                break;
            case 1:  //游戏
                Bundle bundle = new Bundle();
                bundle.putString("gameId", ((LiveTypeInfo) mResult.get(position).data).getId());
                jumpToActivity(GameMainActivity.class, bundle);
                break;
            case 2://直播
                Intent intent = new Intent(this, LiveRoomActivity.class);
                intent.putExtra("id", ((LiveInfo) mResult.get(position).data).getId() + "");
                // intent.putExtra("rtmp", ((LiveInfo) mData.get(position).data).getRtmp());
                jumpToActivity(intent);
                break;
            case 3:  //主播
                //if (((AnchorInfo) mResult.get(position).data).getState() == 1) {
                    intent = new Intent(this, LiveRoomActivity.class);
                    intent.putExtra("id", ((AnchorInfo) mResult.get(position).data).getId() + "");
                    startActivity(intent);
               /* } else {
                    showToast("主播不在线，去看其他直播吧");
                }*/
                break;
            case 4:   //视频
                intent = new Intent(this, PlayVideoActivity.class);
                intent.putExtra("videoId", ((LiveInfo) mResult.get(position).data).getId() + "");
                intent.putExtra("rtmp", ((LiveInfo) mResult.get(position).data).getRtmp());
                jumpToActivity(intent);
                break;
        }
    }

    private void search(String key) {
        Map<String, String> params = new HashMap<>();
        params.put("keyWords", key);
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("type", TYPE);
        sendHttpRequest(API.SEARCH, params);
    }

    /**
     * 将s搜索到数据处理成符合要求的数据
     */
    private void initData(List<LiveTypeInfo> games, List<LiveInfo> lives, List<AnchorInfo> anchors, List<LiveInfo> videos) {
        if (games != null && !games.isEmpty()) {
            mResult.add(new SearchWrapper<Object>(0, "相关游戏"));
            for (int i = 0; i < 4 && i < games.size(); i++) {
                mResult.add(new SearchWrapper<Object>(1, games.get(i)));
            }
        }
        if (lives != null && !lives.isEmpty()) {
            mResult.add(new SearchWrapper<Object>(0, "相关直播" + lives.size()));
            for (int i = 0; i < 4 && i < lives.size(); i++) {
                mResult.add(new SearchWrapper<Object>(2, lives.get(i)));
            }
        }

        if (anchors != null && !anchors.isEmpty()) {
            mResult.add(new SearchWrapper<Object>(0, "相关主播" + anchors.size()));
            for (int i = 0; i < 4 && i < anchors.size(); i++) {
                mResult.add(new SearchWrapper<Object>(3, anchors.get(i)));
            }
        }

        if (videos != null && !videos.isEmpty()) {
            mResult.add(new SearchWrapper<Object>(0, "相关视频" + videos.size()));
            for (int i = 0; i < 4 && i < videos.size(); i++) {
                mResult.add(new SearchWrapper<Object>(4, videos.get(i)));
            }
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.SEARCH:
                    if (object.getJSONObject("object").has("game") && object.getJSONObject("object").getJSONObject("game").has("list")) {
                        games = gson.fromJson(object.getJSONObject("object").
                                        getJSONObject("game").getJSONArray("list").toString(),
                                new TypeToken<List<LiveTypeInfo>>() {
                                }.getType());
                    }
                    if (object.getJSONObject("object").has("liveOnLive") && object.getJSONObject("object").getJSONObject("liveOnLive").has("list")) {
                        lives = gson.fromJson(object.getJSONObject("object").
                                        getJSONObject("liveOnLive").getJSONArray("list").toString(),
                                new TypeToken<List<LiveInfo>>() {
                                }.getType());
                    }
                    if (object.getJSONObject("object").has("liveUp") && object.getJSONObject("object").getJSONObject("liveUp").has("list")) {
                        anchors = gson.fromJson(object.getJSONObject("object").
                                        getJSONObject("liveUp").getJSONArray("list").toString(),
                                new TypeToken<List<AnchorInfo>>() {
                                }.getType());
                    }
                    if (object.getJSONObject("object").has("liveVideo") && object.getJSONObject("object").getJSONObject("liveVideo").has("list")) {
                        videos = gson.fromJson(object.getJSONObject("object").
                                        getJSONObject("liveVideo").getJSONArray("list").toString(),
                                new TypeToken<List<LiveInfo>>() {
                                }.getType());
                    }
                    initData(games, lives, anchors, videos);
                    mAdapter.notifyDataSetChanged();
                    if (mResult == null || mResult.isEmpty()) {
                        showErrorPage("啥都没搜到", 0);
                    }else {
                        hideErrorPage();
                    }
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
