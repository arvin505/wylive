package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionAnchorAdapter;
import com.miqtech.wymaster.wylive.proxy.UserEventDispatcher;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/22.
 * 关注主播列表
 */
@LayoutId(R.layout.fragment_attention_anchor)
public class FragmentAttentionAnchor extends BaseFragment implements RecycleViewItemClickListener {
    @BindView(R.id.ptr_attention_anchor)
    PullToRefreshRecyclerView ptrAttenAnchor;

    RecyclerView rvAttenAnchor;

    LinearLayoutManager layoutManager;

    List<AnchorInfo> mDatas;

    AttentionAnchorAdapter mAdapter;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {

        rvAttenAnchor = ptrAttenAnchor.getRefreshableView();
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);

        rvAttenAnchor.setLayoutManager(layoutManager);

        generateData();

        mAdapter = new AttentionAnchorAdapter(mActivity, mDatas);

        rvAttenAnchor.setAdapter(mAdapter);
        rvAttenAnchor.addItemDecoration(new DividerGridItemDecoration(mActivity));
        mAdapter.setOnItemClickListener(this);

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

    private void loadAttenAnchorData(){
        User user = UserProxy.getUser();
        UserEventDispatcher.getAttentionAnchor(this);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        showErrorView(true);
    }


    @Override
    public void onItemClick(View view, int position) {
        showToast("click : " + position);
    }
}
