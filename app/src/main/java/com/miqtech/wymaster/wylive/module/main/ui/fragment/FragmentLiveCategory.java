package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.media.MediaDataSource;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveCategory;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.LiveCategoryAdapter;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/20.
 * 直播分类
 */
@LayoutId(R.layout.fragment_livecategory)
@Title(title = "分类")
public class FragmentLiveCategory extends BaseFragment implements RecycleViewItemClickListener {
    @BindView(R.id.ptrCategory)
    PullToRefreshRecyclerView ptrCategory;

    RecyclerView rvCategory;

    GridLayoutManager layoutManager;

    private LiveCategory mDatas = new LiveCategory();
    private LiveCategoryAdapter mAdapter;

    private int page = 1;
    private int pageSize = 12;

    private int isLast = 0;


    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        rvCategory = ptrCategory.getRefreshableView();

        layoutManager = new GridLayoutManager(mActivity, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                }
                if (mDatas.getHistory() == null || mDatas.getHistory().isEmpty()) {
                    return 1;
                } else {
                    if (position == 1) {
                        return 3;
                    } else {
                        return 1;
                    }
                }
            }
        });
        rvCategory.setLayoutManager(layoutManager);
        rvCategory.setPadding(DeviceUtils.dp2px(mActivity, 5), 0, DeviceUtils.dp2px(mActivity, 5), 0);
        //generateData();
        mAdapter = new LiveCategoryAdapter(mActivity, mDatas);
        rvCategory.setAdapter(mAdapter);
        mAdapter.setOnRecentItemClickListener(new LiveCategoryAdapter.OnRecentItemClickListener() {
            @Override
            public void onRecentItemClick(View view, int pos) {
                showToast("recentt : " + pos);
            }
        });
        mAdapter.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();

        loadCategoryData();

        ptrCategory.setMode(PullToRefreshBase.Mode.BOTH);
        ptrCategory.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadCategoryData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 1) {
                    showToast("已经到底啦");
                    ptrCategory.onRefreshComplete();
                } else {
                    page++;
                    loadCategoryData();
                }

            }
            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });
    }

    private void generateData() {
        mDatas = new LiveCategory();
        List<LiveTypeInfo> recents = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            info.setIcon("http://img2.imgtn.bdimg.com/it/u=582911641,3598372165&fm=21&gp=0.jpg");
            info.setVideoNum(4545);
            info.setLiveNum(784);
            info.setName("狗熊联盟");
            recents.add(info);
        }
        mDatas.setHistory(recents);

        List<LiveTypeInfo> all = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            info.setIcon("http://img2.imgtn.bdimg.com/it/u=582911641,3598372165&fm=21&gp=0.jpg");
            info.setVideoNum(4545);
            info.setLiveNum(784);
            info.setName("狗熊联盟");
            all.add(info);
        }
        //mDatas.setHistory(all);
    }

    @Override
    public void onItemClick(View view, int position) {
        showToast("click " + position);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrCategory.onRefreshComplete();
        try {
            switch (method) {
                case API.CATEGORY:
                    Gson gson = new Gson();
                    if (page == 1) {
                        List<LiveTypeInfo> history = gson.fromJson(object.getJSONObject("object").getJSONArray("history").toString(),
                                new TypeToken<List<LiveTypeInfo>>() {
                                }.getType());
                        mDatas.setHistory(history);
                        mDatas.setGameList(new ArrayList<LiveTypeInfo>());
                    }
                    List<LiveTypeInfo> gamelist = gson.fromJson(object.getJSONObject("object").getJSONObject("gameList")
                                    .getJSONArray("list").toString(),
                            new TypeToken<List<LiveTypeInfo>>() {
                            }.getType());
                    mDatas.getGameList().addAll(gamelist);
                    if (page == 1) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                    }
                    if ((gamelist == null || gamelist.isEmpty()) && page > 1) {
                        page--;
                    }
                    isLast = object.getJSONObject("object").getJSONObject("gameList").getInt("isLast");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        ptrCategory.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        ptrCategory.onRefreshComplete();
    }

    /**
     * 请求分类数据
     */
    private void loadCategoryData() {
        User user = UserProxy.getUser();
        Map<String, String> params = new HashMap<>();
        if (user != null) {
            params.put("userId", user.getId());
            params.put("token", user.getToken());
        }
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        sendHttpRequest(API.CATEGORY, params);
    }
}
