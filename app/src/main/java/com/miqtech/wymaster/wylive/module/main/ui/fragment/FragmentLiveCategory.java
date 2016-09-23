package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.media.MediaDataSource;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
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
import com.miqtech.wymaster.wylive.module.game.activity.GameMainActivity;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.LiveCategoryAdapter;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONException;
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
    private int pageSize = 9;
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
        rvCategory.setPadding(DeviceUtils.dp2px(mActivity, 3), 0, DeviceUtils.dp2px(mActivity, 3), 0);
        //generateData();
        mAdapter = new LiveCategoryAdapter(mActivity, mDatas);
        rvCategory.setAdapter(mAdapter);
        mAdapter.setOnRecentItemClickListener(new LiveCategoryAdapter.OnRecentItemClickListener() {
            @Override
            public void onRecentItemClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putString("gameId", mDatas.getHistory().get(position).getId());
                jumpToActivity(GameMainActivity.class, bundle);
            }
        });
        mAdapter.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();

        loadCategoryData();

        ptrCategory.setMode(PullToRefreshBase.Mode.BOTH);
        ptrCategory.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                mAdapter.setItemVisible(false);
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
        Bundle bundle = new Bundle();
        bundle.putString("gameId", mDatas.getGameList().get(position).getId());
        jumpToActivity(GameMainActivity.class, bundle);
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrCategory.onRefreshComplete();
        try {
            switch (method) {
                case API.CATEGORY:
                    if (rvCategory.getChildAt(0) instanceof ViewGroup)
                        hideItem((ViewGroup) rvCategory.getChildAt(0));
                    Gson gson = new Gson();
                    if (page == 1 && object.getJSONObject("object").has("history")) {
                        List<LiveTypeInfo> history = gson.fromJson(object.getJSONObject("object").getJSONArray("history").toString(),
                                new TypeToken<List<LiveTypeInfo>>() {
                                }.getType());
                        mDatas.setHistory(history);

                    }
                    List<LiveTypeInfo> gamelist = gson.fromJson(object.getJSONObject("object").getJSONObject("gameList")
                                    .getJSONArray("list").toString(),
                            new TypeToken<List<LiveTypeInfo>>() {
                            }.getType());
                    if (page == 1) {
                        mDatas.setGameList(gamelist);
                        mAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recycleAnimate(rvCategory);
                                        if (rvCategory.getChildAt(0) instanceof ViewGroup)
                                            mAdapter.animeRecent((ViewGroup) rvCategory.getChildAt(0));
                                        mAdapter.setItemVisible(true);
                                    }
                                });
                            }
                        }, 300);
                    } else {
                        mDatas.getGameList().addAll(gamelist);
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
        //   ptrCategory.onRefreshComplete();;
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        //      ptrCategory.onRefreshComplete();
        try {
            if (object.getInt("code") == -1)
                loadCategoryDataNoUser();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        } else {
            loadCategoryDataNoUser();
            return;
        }
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("type", 2 + "");
        sendHttpRequest(API.CATEGORY, params);
    }

    /**
     * 请求分类数据
     */
    private void loadCategoryDataNoUser() {
        Map<String, String> params = new HashMap<>();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        sendHttpRequest(API.CATEGORY, params);
    }

    private void hideItem(ViewGroup viewGroup) {
        if (viewGroup == null) return;
        ViewGroup content = (ViewGroup) viewGroup.findViewById(R.id.llRecentContent);
        if (content == null) return;
        for (int i = 0; i < content.getChildCount(); i++) {
            View child = content.getChildAt(i);
            child.setVisibility(View.INVISIBLE);
        }
    }

    private void recycleAnimate(RecyclerView recyclerView) {
        if (recyclerView == null || recyclerView.getChildCount() == 0) return;
        int childCount = recyclerView.getChildCount();
        AnimatorSet set = new AnimatorSet();
        List<Animator> animators = new ArrayList<>(20);
        int start = (mDatas.getHistory() != null && !mDatas.getHistory().isEmpty()) ? 2 : 1;
        for (int i = start; i < childCount; i++) {
            final View child = recyclerView.getChildAt(i);
            PropertyValuesHolder pvhx = PropertyValuesHolder.ofFloat("scaleX", 0.4f, 1f);
            PropertyValuesHolder pvhy = PropertyValuesHolder.ofFloat("scaleY", 0.4f, 1f);
            ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(child, pvhx, pvhy);
            animator.setStartDelay(i * 40);
            animator.setInterpolator(new BounceInterpolator());
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    child.setVisibility(View.VISIBLE);
                }
            });
            animator.setDuration(1000);
            animator.start();
            animators.add(animator);
        }
        Log.e(TAG, "count ---- " + childCount);
        set.setDuration(1000);
        set.playTogether(animators);
        set.start();
    }
}
