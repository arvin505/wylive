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
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.entity.LiveCategory;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.LiveCategoryAdapter;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
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
public class FragmentLiveCategory extends BaseFragment implements RecycleViewItemClickListener {
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
        rvCategory.setPadding(DeviceUtils.dp2px(mActivity, 5), 0, DeviceUtils.dp2px(mActivity, 5), 0);
        generateData();
        mAdapter = new LiveCategoryAdapter(mActivity, mData);
        rvCategory.setAdapter(mAdapter);
        mAdapter.setOnRecentItemClickListener(new LiveCategoryAdapter.OnRecentItemClickListener() {
            @Override
            public void onRecentItemClick(View view, int pos) {
                showToast("recentt : " + pos);
            }
        });
        mAdapter.setOnItemClickListener(this);
        mAdapter.notifyDataSetChanged();
    }

    private void generateData() {
        mData = new LiveCategory();
        List<LiveTypeInfo> recents = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            info.setIcon("http://img2.imgtn.bdimg.com/it/u=582911641,3598372165&fm=21&gp=0.jpg");
            info.setVideoNum(4545);
            info.setLiveNum(784);
            info.setName("狗熊联盟");
            recents.add(info);
        }
        mData.setRecentPlay(recents);

        List<LiveTypeInfo> all = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LiveTypeInfo info = new LiveTypeInfo();
            info.setIcon("http://img2.imgtn.bdimg.com/it/u=582911641,3598372165&fm=21&gp=0.jpg");
            info.setVideoNum(4545);
            info.setLiveNum(784);
            info.setName("狗熊联盟");
            all.add(info);
        }
        mData.setAllPlay(all);
    }

    @Override
    public void onItemClick(View view, int position) {
        showToast("click " + position);
    }
}
