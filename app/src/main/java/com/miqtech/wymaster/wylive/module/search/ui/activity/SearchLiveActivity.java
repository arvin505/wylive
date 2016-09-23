package com.miqtech.wymaster.wylive.module.search.ui.activity;

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
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.module.live.PlayVideoActivity;
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
 */
@LayoutId(R.layout.activity_search_anchor)
@Title(title = "相关直播")
public class SearchLiveActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    @BindView(R.id.ptr_search_anchor)
    PullToRefreshRecyclerView ptrSearchAnchor;
    RecyclerView rvSearchAnchor;

    LinearLayoutManager layoutManager;

    List<LiveInfo> mDatas = new ArrayList<>();

    VideoListAdapter mAdapter;

    private int isLast = 0;

    private String TYPE = "4";
    private int page = 1;
    private int pageSize = 12;
    private String key;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        key = getIntent().getExtras().getString("key");
        rvSearchAnchor = ptrSearchAnchor.getRefreshableView();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvSearchAnchor.setLayoutManager(layoutManager);
        search(key);
        mAdapter = new VideoListAdapter(this, mDatas);

        rvSearchAnchor.setAdapter(mAdapter);
        rvSearchAnchor.addItemDecoration(new DividerGridItemDecoration(this));
        mAdapter.setOnItemClickListener(this);

        ptrSearchAnchor.setMode(PullToRefreshBase.Mode.BOTH);
        ptrSearchAnchor.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                search(key);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 1) {
                    showToast("已经到底啦");
                    ptrSearchAnchor.onRefreshComplete();
                } else {
                    page++;
                    search(key);
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
        intent.putExtra("videoId",mDatas.get(position).getId()+"");
        intent.putExtra("rtmp",mDatas.get(position).getRtmp()+"");
        jumpToActivity(intent);
    }

    private void search(String key) {
        Map<String, String> params = new HashMap<>();
        params.put("keyWords", key);
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("type", TYPE);
        sendHttpRequest(API.SEARCH, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrSearchAnchor.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.SEARCH:
                    List<LiveInfo> data = gson.fromJson(object.getJSONObject("object").getJSONObject("liveOnLive").getJSONArray("list")
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
                    isLast = object.getJSONObject("object").getJSONObject("liveOnLive").getInt("isLast");
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
