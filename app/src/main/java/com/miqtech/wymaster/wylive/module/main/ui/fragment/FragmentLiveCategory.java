package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.entity.LiveCategory;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.LiveCategoryAdapter;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/20.
 * 直播分类
 */
@LayoutId(R.layout.fragment_livecategory)
@Title(title = "分类")
public class FragmentLiveCategory extends BaseFragment {
    @BindView(R.id.prCategory)
    PullToRefreshRecyclerView prCategory;

    RecyclerView rvCategory;

    GridLayoutManager layoutManager;

    private LiveCategory mData;
    private LiveCategoryAdapter mAdapter;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        rvCategory = prCategory.getRefreshableView();

        layoutManager = new GridLayoutManager(mActivity, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 3;
                }
                if (mData.getRecentPlay() == null || mData.getRecentPlay().isEmpty()) {
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

        generateData();
        mAdapter = new LiveCategoryAdapter(mActivity,mData);
        rvCategory.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void generateData() {
        mData = new LiveCategory();
        List<LiveTypeInfo> recents = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            recents.add(info);
        }
        mData.setRecentPlay(recents);

        List<LiveTypeInfo> all = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            all.add(info);
        }
        mData.setAllPlay(all);
    }
}
