package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionAnchorAdapter;
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
 * 关注主播列表
 */
@LayoutId(R.layout.fragment_attention_anchor)
public class FragmentAttentionAnchor extends BaseFragment implements RecycleViewItemClickListener, UserProxy.OnUserChangeListener, Observerable.ISubscribe {
    @BindView(R.id.ptr_attention_anchor)
    PullToRefreshRecyclerView ptrAttenAnchor;

    RecyclerView rvAttenAnchor;

    LinearLayoutManager layoutManager;

    List<AnchorInfo> mDatas = new ArrayList<>();

    AttentionAnchorAdapter mAdapter;

    private String type = "1";
    private int page = 1;
    private int pageSize = 12;
    private int isLast = 0;

    private boolean first = true;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        UserProxy.addListener(this);
        rvAttenAnchor = ptrAttenAnchor.getRefreshableView();
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        rvAttenAnchor.setLayoutManager(layoutManager);

        //generateData();

        mAdapter = new AttentionAnchorAdapter(mActivity, mDatas);

        rvAttenAnchor.setAdapter(mAdapter);
        rvAttenAnchor.addItemDecoration(new DividerGridItemDecoration(mActivity));
        mAdapter.setOnItemClickListener(this);

        ptrAttenAnchor.setMode(PullToRefreshBase.Mode.BOTH);
        ptrAttenAnchor.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadAttenAnchorData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 1) {
                    ptrAttenAnchor.onRefreshComplete();
                    showToast("已经到底啦");
                } else {
                    page++;
                    loadAttenAnchorData();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });
        Observerable.getInstance().subscribe(ObserverableType.ATTENTION_ANCHOR, this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && first) {
            loadAttenAnchorData();
            first = false;
        }
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

    private void loadAttenAnchorData() {
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
            if (ptrAttenAnchor != null)
                ptrAttenAnchor.onRefreshComplete();
            jumpToActivity(LoginActivity.class);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        ptrAttenAnchor.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.LIVE_SUBCRIBELIST:
                    List<AnchorInfo> data = gson.fromJson(object.getJSONObject("object")
                                    .getJSONArray("list").toString(),
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
                    isLast = object.getJSONObject("object").getInt("isLast");
                    if (data == null || data.isEmpty()) {
                        if (page > 1) {
                            page--;
                        }
                    }
                    if (mDatas == null || mDatas.isEmpty()) {
                        showErrorPage("快去关注喜欢的主播吧", 0);
                    } else {
                        hideErrorPage();
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
        ptrAttenAnchor.onRefreshComplete();
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        ptrAttenAnchor.onRefreshComplete();
    }

    @Override
    public void onItemClick(View view, int position) {
        // if (mDatas.get(position).getState() == 1) {
        Intent intent = new Intent(mActivity, LiveRoomActivity.class);
        intent.putExtra("id", mDatas.get(position).getId() + "");
        startActivity(intent);
        /*} else {
            showToast("主播不在线，去看其他直播吧");
        }*/
    }


    @Override
    public void onUserChange(User user) {
        if (user != null) {
            loadAttenAnchorData();
        } else {
            mDatas.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        UserProxy.removeListener(this);
        Observerable.getInstance().unSubscribe(ObserverableType.ATTENTION_ANCHOR, this);
        super.onDestroy();
    }

    @Override
    public <T> void update(T... data) {
        AnchorInfo info = (AnchorInfo) data[0];
        for (int i = 0; i < mDatas.size(); i++) {
            if (mDatas.get(i).getId().equals(info.getId())) {
                mDatas.remove(i);
                mAdapter.notifyDataSetChanged();
                return;
            }
        }
        mDatas.add(info);
        mAdapter.notifyItemInserted(mAdapter.getItemCount());
    }
}
