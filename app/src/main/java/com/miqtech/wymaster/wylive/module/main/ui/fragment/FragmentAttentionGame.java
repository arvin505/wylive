package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.LoginActivity;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionGameAdapter;
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
public class FragmentAttentionGame extends BaseFragment implements RecycleViewItemClickListener {
    @BindView(R.id.ptr_attention_game)
    PullToRefreshRecyclerView ptrAttenGame;
    RecyclerView rvAttenGame;
    AttentionGameAdapter mAdapter;
    GridLayoutManager layoutManager;
    List<LiveTypeInfo> mDatas;

    private String type = "1";
    private int page = 1;
    private int pageSize = 12;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        rvAttenGame = ptrAttenGame.getRefreshableView();
        layoutManager = new GridLayoutManager(mActivity, 3);
        generateData();
        mAdapter = new AttentionGameAdapter(mActivity, mDatas);
        rvAttenGame.setLayoutManager(layoutManager);
        rvAttenGame.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);

        loadAttenGameData();

        ptrAttenGame.setMode(PullToRefreshBase.Mode.BOTH);
        ptrAttenGame.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadAttenGameData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page++;
                loadAttenGameData();
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });
    }

    private void generateData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            info.setLiveNum(232 * i + 123);
            info.setIcon("http://img1.imgtn.bdimg.com/it/u=4032670072,53183115&fm=206&gp=0.jpg");
            info.setName("超级玛丽奥" + i);
            info.setVideoNum(122 * i + 123);
            mDatas.add(info);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        showToast("click : position " + position);
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
                    List<LiveTypeInfo> data = gson.fromJson(object.getJSONObject("object").getJSONObject("list")
                                    .getJSONArray("liveUp").toString(),
                            new TypeToken<List<LiveTypeInfo>>() {
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
}
