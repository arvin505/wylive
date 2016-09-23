package com.miqtech.wymaster.wylive.module.live;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
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
import com.miqtech.wymaster.wylive.module.search.ui.adapter.VideoListAdapter;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/28.
 * 相关game下的全部视频
 */
@LayoutId(R.layout.activity_search_anchor)
@Title(title = "全部视频")
public class VideoListActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    @BindView(R.id.ptr_search_anchor)
    PullToRefreshRecyclerView ptrSearchAnchor;
    RecyclerView rvSearchAnchor;

    LinearLayoutManager layoutManager;

    List<LiveInfo> mDatas = new ArrayList<>();

    VideoListAdapter mAdapter;

    private int isLast = 0;

    private int page = 1;
    private int pageSize = 12;
    private String gameId;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        gameId = getIntent().getExtras().getString("gameId");
        rvSearchAnchor = ptrSearchAnchor.getRefreshableView();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSearchAnchor.setLayoutManager(layoutManager);
        loadVideoData(gameId);
        mAdapter = new VideoListAdapter(this, mDatas);

        rvSearchAnchor.setAdapter(mAdapter);
        rvSearchAnchor.addItemDecoration(new DividerGridItemDecoration(this));
        mAdapter.setOnItemClickListener(this);

        ptrSearchAnchor.setMode(PullToRefreshBase.Mode.BOTH);
        ptrSearchAnchor.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadVideoData(gameId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 1) {
                    showToast("已经到底啦");
                    ptrSearchAnchor.onRefreshComplete();
                } else {
                    page++;
                    loadVideoData(gameId);
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(this, PlayVideoActivity.class);
        intent.putExtra("videoId", mDatas.get(position).getId() + "");
        intent.putExtra("rtmp",mDatas.get(position).getRtmp());
        jumpToActivity(intent);
    }

    private void loadVideoData(String gameId) {
        Map<String, String> params = new HashMap<>();
        params.put("gameId", gameId);
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("type", "1");
        sendHttpRequest(API.VIDEO_LIST, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrSearchAnchor.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.VIDEO_LIST:
                    List<LiveInfo> data = gson.fromJson(object.getJSONObject("object").getJSONArray("hotLive")
                                    .toString(),
                            new TypeToken<List<LiveInfo>>() {
                            }.getType());
                    if (page == 1) {
                        mDatas.clear();
                    }
                    mDatas.addAll(data);
                    if (page == 1) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                    }
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (data == null || data.isEmpty()) {
                        if (page > 1) {
                            page--;
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        ptrSearchAnchor.onRefreshComplete();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        ptrSearchAnchor.onRefreshComplete();
    }
}
