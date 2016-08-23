package com.miqtech.wymaster.wylive.module.search.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionAnchorAdapter;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/23.
 */
@LayoutId(R.layout.activity_search_anchor)
@Title(title = "相关主播")
public class SearchAnchorActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    @BindView(R.id.ptr_search_anchor)
    PullToRefreshRecyclerView ptrSearchAnchor;
    RecyclerView rvSearchAnchor;

    LinearLayoutManager layoutManager;

    List<AnchorInfo> mDatas;

    AttentionAnchorAdapter mAdapter;

    private String type = "1";
    private int page = 1;
    private int pageSize = 12;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        rvSearchAnchor = ptrSearchAnchor.getRefreshableView();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rvSearchAnchor.setLayoutManager(layoutManager);

        generateData();

        mAdapter = new AttentionAnchorAdapter(this, mDatas);

        rvSearchAnchor.setAdapter(mAdapter);
        rvSearchAnchor.addItemDecoration(new DividerGridItemDecoration(this));
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

    @Override
    public void onItemClick(View view, int position) {
        showToast(position + "");
    }
}
