package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.Banner;
import com.miqtech.wymaster.wylive.entity.LiveAndVideoData;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.live.adapter.HallAdapter;
import com.miqtech.wymaster.wylive.module.screenrecorder.ui.ScreenRecorderActivity;
import com.miqtech.wymaster.wylive.module.search.ui.activity.SearchActivity;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.widget.HeadLinesView;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by liuxiong on 2016/8/22.
 * 直播首页
 */
@LayoutId(R.layout.fragment_hallcategory)
@Title(title = "娱儿TV")
public class FragmentHallCategory extends BaseFragment implements UserProxy.OnUserChangeListener ,Observerable.ISubscribe{
    @BindView(R.id.prrvHall)
    PullToRefreshRecyclerView prrvHall;
    @BindView(R.id.ibLeft)
    ImageButton imgSearch;
    @BindView(R.id.ibRight)
    ImageButton imgLiveGo;
    RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private HallAdapter adapter;
    private List<LiveInfo> liveDatas = new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> videoDatas = new ArrayList<LiveInfo>();//视频数据
    private LiveAndVideoData liveAndVideoData;
    private List<Banner> banners = new ArrayList<Banner>();
    private boolean isBlack = true;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        checkLive();
        UserProxy.addListener(this);
        imgSearch.setVisibility(View.VISIBLE);
        imgSearch.setImageResource(R.drawable.icon_bar_search);
        imgLiveGo.setImageResource(R.drawable.icon_live_go);
        recyclerView = prrvHall.getRefreshableView();
        layoutManager = new GridLayoutManager(mActivity, 2);
        prrvHall.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // TODO 下拉刷新请求
                loadHallBannerData();
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
        Observerable.getInstance().subscribe(ObserverableType.VIDEO_TIMES,this);
        //请求数据
        loadHallBannerData();
    }

    private void initData() {
        sendHttpRequest(API.LIVE_VIDEO_LIST, null);
    }

    /**
     * banner接口请求
     */
    private void loadHallBannerData() {
        L.e(TAG, "loadHallBannerData");
        sendHttpRequest(API.HALL_BANNER, null);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prrvHall.onRefreshComplete();
        L.e(TAG, "onSuccess" + method);
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
                        L.e(TAG, "onSuccess " + liveDatas.size() + ":::" + videoDatas.size());
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (API.HALL_BANNER.equals(method)) {
            initAData(object);
        } else if (API.LIVE_CHECK.equals(method)) {
            try {
                if (object.getInt("object") == 1) {
                    isBlack = false;
                    if (!checkUp()) {
                        imgLiveGo.setVisibility(View.GONE);
                    } else {
                        imgLiveGo.setVisibility(View.VISIBLE);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 广告接口数
     */
    private void initAData(JSONObject object) {
        banners.clear();
        L.e(TAG, "initAData  开始解析数据");
        try {
            if (object.getInt("code") == 0 && object.has("object")) {
                Gson gs = new Gson();
                banners = gs.fromJson(object.getString("object").toString(), new TypeToken<List<Banner>>() {
                }.getType());
                adapter.setBannerData(banners);
                initData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        L.e(TAG, "onError" + method + ":::" + errMsg);
        prrvHall.onRefreshComplete();
        adapter.setNetWorkState(false);
        //setErrorView();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        L.e(TAG, "onFaild");
        prrvHall.onRefreshComplete();
        try {
            if (object.has("result")) {
                showToast(object.getString("result"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //   setErrorView();
    }

    @OnClick({R.id.ibLeft, R.id.ibRight})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibLeft:
                jumpToActivity(SearchActivity.class);
//                recycleAnimate(recyclerView);
                break;
            case R.id.ibRight:
                jumpToActivity(ScreenRecorderActivity.class);
                break;
        }
    }

    @Override
    public void onUserChange(User user) {
        if (checkUp()) {
            imgLiveGo.setVisibility(View.VISIBLE);
        } else {
            imgLiveGo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        UserProxy.removeListener(this);
        Observerable.getInstance().unSubscribe(ObserverableType.VIDEO_TIMES,this);
        super.onDestroy();
    }

    private boolean checkUp() {
        if (isBlack) return false;
        if (Build.VERSION.SDK_INT < 21) return false;
        User user = UserProxy.getUser();
        if (user == null || user.getIsUp() != 1) return false;
        return true;
    }

    /**
     * 直播机型校验
     */
    private void checkLive() {
        sendHttpRequest(API.LIVE_CHECK, null);
    }

    private void recycleAnimate(RecyclerView recyclerView) {
        int childCount = recyclerView.getChildCount();
        AnimatorSet set = new AnimatorSet();
        List<Animator> animators = new ArrayList<>(20);
        for (int i = 1; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            PropertyValuesHolder pvhx = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
            PropertyValuesHolder pvhy = PropertyValuesHolder.ofFloat("scaleY", 0f, 1f);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(child, pvhx, pvhy);

            animator.setDuration(1000);
            animator.start();
            /*animators.add(animator);*/
        }
    }

    @Override
    public <T> void update(T... data) {
        int id = (Integer) data[0];
        for (int i = 0; i < videoDatas.size(); i++) {
            if (videoDatas.get(i).getId()==id) {
                videoDatas.get(i).setPlayTimes(videoDatas.get(i).getPlayTimes()+1);
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    public void onDestroyView() {
        HeadLinesView headLinesView = (HeadLinesView) recyclerView.findViewById(R.id.hlvHallBanner);
        headLinesView.stopAutoScroll();
        super.onDestroyView();
    }
}
