package com.miqtech.wymaster.wylive.module.live;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.module.live.adapter.HallAdapter;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by admin on 2016/7/25.
 */
@LayoutId(R.layout.activity_live_layout)
@Title(title="直播列表")
public class LivePlayListActivity extends BaseAppCompatActivity{
    @BindView(R.id.prrvLivePlay)
    PullToRefreshRecyclerView prrvLivePlay;
    RecyclerView recyclerView;

    private int page = 1;
    private Context context;
    private int isLast = 0;
    private GridLayoutManager layoutManager;
    private HallAdapter adapter;
    private List<LiveInfo> liveDatas = new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> videoDatas = new ArrayList<LiveInfo>();//视频数据
    private String gameId = ""; //游戏id

    @Override
    protected void initViews(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gameId = bundle.getString("gameId");
        }
        context = LivePlayListActivity.this;
        initView();
        initData();
    }

    protected void initView() {
        recyclerView = prrvLivePlay.getRefreshableView();
        layoutManager = new GridLayoutManager(context, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case HallAdapter.VIEW_LIVE_ITEM:
                        return 1;
                    case HallAdapter.VIEW_EMPTY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HallAdapter(context, 1, liveDatas, videoDatas);
        prrvLivePlay.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                getInfomations(gameId);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            page++;
                            getInfomations(gameId);
                        }
                    }, 1000);
                } else {
                    showToast("已经到底啦");
                    prrvLivePlay.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });
    }

    protected void initData() {
        getInfomations(gameId);
    }

    /**
     * 获取直播列表数据
     */
    private void getInfomations(String gameId) {
        // showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        if (!gameId.equals("")) {
            params.put("gameId", gameId + "");
        }
        sendHttpRequest(API.LIVE_LIST, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        //   hideLoading();
        prrvLivePlay.onRefreshComplete();
        if (API.LIVE_LIST.equals(method)) {
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    Gson gs = new Gson();
                    List<LiveInfo> datas = gs.fromJson(object.getJSONObject("object").getJSONArray("list").toString(), new TypeToken<List<LiveInfo>>() {
                    }.getType());
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (datas != null && !datas.isEmpty()) {
                        if (page == 1) {
                            liveDatas.clear();
                            liveDatas.addAll(datas);
                            recyclerView.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        //setErrorView();
                    }
                    if (liveDatas==null || liveDatas.isEmpty()){
                        showErrorPage("直播被怪兽抓走了，我们正在全力营救!",0);
                    }else {
                        hideErrorPage();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prrvLivePlay.onRefreshComplete();
        //  hideLoading();
        setErrorView();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prrvLivePlay.onRefreshComplete();
        //   hideLoading();
        adapter.setNetWorkState(false);
        setErrorView();
    }

    /**
     * 设置没有数据的错误页面
     */
    private void setErrorView() {
        liveDatas.clear();
        videoDatas.clear();
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
