package com.miqtech.wymaster.wylive.module.search.ui.activity;

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
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionAnchorAdapter;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/23.
 */
@LayoutId(R.layout.activity_search_anchor)
@Title(title = "相关主播")
public class SearchAnchorActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    @BindView(R.id.ptr_search_anchor)
    PullToRefreshRecyclerView ptrSearchAnchor;
    RecyclerView rvSearchAnchor;

    LinearLayoutManager layoutManager;

    List<AnchorInfo> mDatas;

    AttentionAnchorAdapter mAdapter;

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

        generateData();

        mAdapter = new AttentionAnchorAdapter(this, mDatas);

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

    private void generateData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            AnchorInfo info = new AnchorInfo();
            info.setFans(23234);
            info.setIcon("http://img2.a0bi.com/upload/ttq/20160709/1468043314019.png");
            info.setName("这主播有毒" + i);
            info.setRoom(i * 1233 + 124 + "");
            info.setState(i % 3);
            mDatas.add(info);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        showToast(position + "");
    }

    private void search(String key) {
        Map<String, String> params = new HashMap<>();
        params.put("keyWords", key);
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("type", TYPE);
        sendHttpRequest(API.CATEGORY, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrSearchAnchor.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.LIVE_SUBCRIBELIST:
                    List<AnchorInfo> data = gson.fromJson(object.getJSONObject("object").getJSONObject("liveUp").getJSONArray("list")
                                    .toString(),
                            new TypeToken<List<AnchorInfo>>() {
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
