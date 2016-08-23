package com.miqtech.wymaster.wylive.module.search.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.module.search.ui.activity.adapter.SearchContentAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import butterknife.BindView;

/**
 * Created by xiaoyi on 2016/8/23.
 */
@LayoutId(R.layout.activity_search_content)
@Title(title = "搜索结果")
public class SearchContentActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    String key;
    @BindView(R.id.rvSearch)
    RecyclerView rvSearch;

    List<SearchWrapper<Object>> mResult;
    GridLayoutManager layoutManager;
    SearchContentAdapter mAdapter;


    @Override
    protected void initViews(Bundle savedInstanceState) {
        key = getIntent().getExtras().getString("key");

        generateData();

        mAdapter = new SearchContentAdapter(this, mResult);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mResult.get(position).type == 2) {
                    return 1;
                }
                return 2;
            }
        });
        rvSearch.setLayoutManager(layoutManager);
        rvSearch.setAdapter(mAdapter);
        rvSearch.addItemDecoration(new DividerGridItemDecoration(this));

        mAdapter.setOnItemClickListener(this);
    }

    private void generateData() {
        mResult = new ArrayList<>();
        String tag = new String("游戏");
        SearchWrapper gameTag = new SearchWrapper(0, tag);
        mResult.add(gameTag);
        LiveTypeInfo gameInfo = new LiveTypeInfo();
        gameInfo.setName("王者融入");
        gameInfo.setIcon("http://img2.gtimg.com//dbimg/2013/mainpiclib/20130808/16/bfff5b3eabf813d2952d274919b641422.jpg");
        gameInfo.setVideoNum(1513);
        gameInfo.setLiveNum(4815);
        SearchWrapper game = new SearchWrapper(1, gameInfo);
        mResult.add(game);

        String liveTag = new String("直播");
        SearchWrapper live = new SearchWrapper(0, liveTag);
        mResult.add(live);


        for (int i = 0; i < 4; i++) {
            LiveInfo info = new LiveInfo();

            /*info.setIcon("http://img2.a0bi.com/upload/ttq/20160709/1468043314019.png");
            info.setName("这主播有毒" + i);
            info.setRoom(i * 1233 + 124 + "");
            info.setState(i % 3);*/
            mResult.add(new SearchWrapper<Object>(2, info));

        }

        String anchorTAG = new String("主播");
        SearchWrapper anchor = new SearchWrapper(0, anchorTAG);
        mResult.add(anchor);

        for (int i = 0; i < 4; i++) {
            AnchorInfo info = new AnchorInfo();
            info.setFans(23234);
            info.setIcon("http://img2.a0bi.com/upload/ttq/20160709/1468043314019.png");
            info.setName("这主播有毒" + i);
            info.setRoom(i * 1233 + 124 + "");
            info.setState(i % 3);
            mResult.add(new SearchWrapper<Object>(3, info));

        }
        mResult.add(new SearchWrapper<Object>(0, "视频"));
        for (int i = 0; i < 4; i++) {
            LiveInfo info = new LiveInfo();
            /*info.setFans(23234);
            info.setIcon("http://img2.a0bi.com/upload/ttq/20160709/1468043314019.png");
            info.setName("这主播有毒" + i);
            info.setRoom(i * 1233 + 124 + "");
            info.setState(i % 3);*/
            mResult.add(new SearchWrapper<Object>(4, info));
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        Object data = mResult.get(position).data;
        if (data instanceof String) {
            if (data.equals("主播")) {
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                jumpToActivity(SearchAnchorActivity.class, bundle);
            }
        }
    }
}
