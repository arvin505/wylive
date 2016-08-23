package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.Banner;
import com.miqtech.wymaster.wylive.entity.LiveAndVideoData;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.HallAdapter;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by liuxiong on 2016/8/22.
 * 直播首页
 */
@LayoutId(R.layout.fragment_hallcategory)
@Title(title = "娱儿TV")
public class FragmentHallCategory extends BaseFragment {
    @BindView(R.id.rvContent)
    PullToRefreshRecyclerView rvContent;
    RecyclerView recyclerView;
    private GridLayoutManager  layoutManager;
    private HallAdapter adapter;
    private List<LiveInfo> liveDatas = new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> videoDatas = new ArrayList<LiveInfo>();//视频数据
    private LiveAndVideoData liveAndVideoData;
    private List<Banner> banners = new ArrayList<Banner>();

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        recyclerView = rvContent.getRefreshableView();
        layoutManager = new GridLayoutManager(mActivity, 2);
        rvContent.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // TODO 下拉刷新请求
              //  getInfomations();
                rvContent.onRefreshComplete();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if (!isHasNetWork) {
                    showToast(getString(R.string.noNeteork));
                }
            }
        });
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                //TODO 此处去设置是girdView 还是listView
                L.e(TAG,"getSpanSize"+adapter.getItemViewType(position));
                switch (adapter.getItemViewType(position)) {
                    case HallAdapter.VIEW_BANNER:
                        return 2;
                    case HallAdapter.VIEW_LIVE_ITEM:
                        return 1;
                    case HallAdapter.VIEW_LIVE_TITLE:
                        return 2;
                    case HallAdapter.VIEW_VIDEO_ITEM:
                        return 2;
                    case HallAdapter.VIEW_VIDEO_TITLE:
                        return 2;
                    case HallAdapter.VIEW_EMPTY:
                        return 2;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        adapter = new HallAdapter(getActivity(), 0, liveDatas, videoDatas);
        //请求数据
       loadHallBannerData();
    }
    private void initData(){
        sendHttpRequest(API.LIVE_VIDEO_LIST,null);
    }

    /**
     * banner接口请求
     */
    private void loadHallBannerData() {

        sendHttpRequest(API.HALL_BANNER,null);
    }
    @Override
    public void onSuccess(JSONObject object, String method) {
        rvContent.onRefreshComplete();

      //  hideLoading();
        L.e(TAG,"onSuccess"+method);
        if (API.LIVE_VIDEO_LIST.equals(method)) {
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    Gson gson = new Gson();
                    liveAndVideoData = gson.fromJson(object.getJSONObject("object").toString(), LiveAndVideoData.class);
                    if (liveAndVideoData != null) {
                        if (liveAndVideoData.getOnLive() != null) {
                            liveDatas.clear();
                            liveDatas.addAll(liveAndVideoData.getOnLive());
                        }
                        if (liveAndVideoData.getHotVideo() != null) {
                            videoDatas.clear();
                            videoDatas.addAll(liveAndVideoData.getHotVideo());
                        }
                        L.e(TAG,"onSuccess "+liveDatas.size()+":::"+videoDatas.size());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        //TODO 没有数据显示错误页面
                      //  setErrorView();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(API.HALL_BANNER.equals(method)){
           initAData(object);
        }

    }
    /**
     * 广告接口数据
     */
    private void initAData(JSONObject object) {
        banners.clear();
        L.e(TAG,"initAData  开始解析数据");
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                L.e(TAG,"initAData  开始解析数据");
                Gson gs = new Gson();
                banners = gs.fromJson(object.getString("object").toString(), new TypeToken<List<Banner>>() {}.getType());
                L.e(TAG,"initAData  开始解析数据"+banners.size());
                adapter.setBannerData(banners);
                initData();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        L.e(TAG,"onError"+method);
        rvContent.onRefreshComplete();
        adapter.setNetWorkState(false);
        //setErrorView();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        L.e(TAG,"onFaild"+method);
        rvContent.onRefreshComplete();
     //   hideLoading();
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
     //   setErrorView();
    }
}
