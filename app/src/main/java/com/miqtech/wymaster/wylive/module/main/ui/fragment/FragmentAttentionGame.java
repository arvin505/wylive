package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionGameAdapter;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        rvAttenGame = ptrAttenGame.getRefreshableView();
        layoutManager = new GridLayoutManager(mActivity, 3);
        generateData();
        mAdapter = new AttentionGameAdapter(mActivity, mDatas);
        rvAttenGame.setLayoutManager(layoutManager);
        rvAttenGame.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);


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
}
