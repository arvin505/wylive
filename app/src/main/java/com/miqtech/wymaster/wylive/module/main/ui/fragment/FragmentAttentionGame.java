package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.game.activity.GameMainActivity;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionGameAdapter;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/22.
 */
@LayoutId(R.layout.fragment_attention_game)
public class FragmentAttentionGame extends BaseFragment implements RecycleViewItemClickListener, UserProxy.OnUserChangeListener, Observerable.ISubscribe {
    @BindView(R.id.ptr_attention_game)
    PullToRefreshRecyclerView ptrAttenGame;
    RecyclerView rvAttenGame;
    AttentionGameAdapter mAdapter;
    GridLayoutManager layoutManager;
    List<LiveTypeInfo> mDatas = new ArrayList<>();

    private String type = "2";
    private int page = 1;
    private int pageSize = 12;
    private int isLast = 0;
    private boolean first = true;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        UserProxy.addListener(this);
        rvAttenGame = ptrAttenGame.getRefreshableView();
        layoutManager = new GridLayoutManager(mActivity, 3);
        mAdapter = new AttentionGameAdapter(mActivity, mDatas);
        rvAttenGame.setLayoutManager(layoutManager);
        rvAttenGame.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        ptrAttenGame.setMode(PullToRefreshBase.Mode.BOTH);
        ptrAttenGame.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadAttenGameData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 1) {
                    ptrAttenGame.onRefreshComplete();
                    showToast("已经到底啦");
                } else {
                    page++;
                    loadAttenGameData();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });

        Observerable.getInstance().subscribe(ObserverableType.ATTENTION_GAME, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && first) {
            loadAttenGameData();
            first = false;
        }
    }

    /*private void generateData() {
        for (int i = 0; i < 10; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            info.setLiveNum(232 * i + 123);
            info.setIcon("http://img1.imgtn.bdimg.com/it/u=4032670072,53183115&fm=206&gp=0.jpg");
            info.setName("超级玛丽奥" + i);
            info.setVideoNum(122 * i + 123);
            mDatas.add(info);
        }
    }*/

    @Override
    public void onItemClick(View view, int position) {
        Bundle bundle = new Bundle();
        bundle.putString("gameId", mDatas.get(position).getId());
        jumpToActivity(GameMainActivity.class, bundle);
    }

    private void loadAttenGameData() {
        User user = UserProxy.getUser();
        if (user != null) {
            Map<String, String> params = new HashMap<>();
            params.put("token", user.getToken());
            params.put("userId", user.getId());
            params.put("type", type);
            params.put("page", page + "");
            params.put("pageSize", pageSize + "");
            sendHttpRequest(API.LIVE_SUBCRIBELIST, params);
        } else {
            if (ptrAttenGame != null)
                ptrAttenGame.onRefreshComplete();
            jumpToActivity(LoginActivity.class);
        }
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrAttenGame.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.LIVE_SUBCRIBELIST:
                    List<LiveTypeInfo> data = gson.fromJson(object.getJSONObject("object").getJSONArray("list")
                                    .toString(),
                            new TypeToken<List<LiveTypeInfo>>() {
                            }.getType());
                    if (page == 1) {
                        mDatas.clear();
                    }
                    mDatas.addAll(data);
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (page == 1) {
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mAdapter.notifyItemInserted(mAdapter.getItemCount());
                    }
                    if (page > 1) {
                        if (data == null || data.isEmpty()) page--;
                    }
                    if (mDatas == null || mDatas.isEmpty()) {
                        showErrorPage("快去收藏喜欢的游戏吧", 0);
                    } else {
                        hideErrorPage();
                    }
                   // generateData();
                    mAdapter.notifyDataSetChanged();
                    Log.e(TAG,"count   " +mAdapter.getItemCount());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        ptrAttenGame.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        ptrAttenGame.onRefreshComplete();
    }

    @Override
    public void onUserChange(User user) {
        if (user != null) {
            loadAttenGameData();
        } else {
            mDatas.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        UserProxy.removeListener(this);
        Observerable.getInstance().unSubscribe(ObserverableType.ATTENTION_GAME, this);
        super.onDestroy();
    }

    @Override
    public <T> void update(T... data) {
        LiveTypeInfo info = (LiveTypeInfo) data[0];
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId().equals(info.getId())) {
                mDatas.remove(i);
                mAdapter.notifyDataSetChanged();
                if (mDatas == null || mDatas.isEmpty()) {
                    showErrorPage("快去收藏喜欢的游戏吧", 0);
                } else {
                    hideErrorPage();
                }
                return;
            }
        }
        mDatas.add(info);
        mAdapter.notifyItemInserted(mAdapter.getItemCount());
    }
}
