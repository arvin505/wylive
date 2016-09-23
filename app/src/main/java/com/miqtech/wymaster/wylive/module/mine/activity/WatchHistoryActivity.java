package com.miqtech.wymaster.wylive.module.mine.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.DividerGridItemDecoration;
import com.miqtech.wymaster.wylive.common.RecycleViewItemClickListener;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.main.ui.adapter.AttentionAnchorAdapter;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 观看历史
 * Created by wuxn on 2016/8/26.
 */
@LayoutId(R.layout.activity_watchhistory)
public class WatchHistoryActivity extends BaseAppCompatActivity implements RecycleViewItemClickListener {
    @BindView(R.id.prrvWatchHistory)
    PullToRefreshRecyclerView prrvWatchHistory;

    @BindView(R.id.tvRightHandle)
    TextView tvRightHandle;

    RecyclerView rvWatchHostory;

    LinearLayoutManager layoutManager;

    List<AnchorInfo> mDatas = new ArrayList<>();

    private AttentionAnchorAdapter mAdapter;

    private HashMap<String, String> params = new HashMap<>();

    private int page = 1;
    private int pageSize = 12;
    private int isLast = 0;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("观看历史");
        rvWatchHostory = prrvWatchHistory.getRefreshableView();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvWatchHostory.setLayoutManager(layoutManager);
        mAdapter = new AttentionAnchorAdapter(this, mDatas);
        rvWatchHostory.setAdapter(mAdapter);
        loadWatchHistory();
        tvRightHandle.setText("清除历史");
        tvRightHandle.setTextSize(12);
        tvRightHandle.setVisibility(View.VISIBLE);
        tvRightHandle.setTextColor(getResources().getColor(R.color.bar_text_selected));
        tvRightHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCleanCacheDialog();
            }
        });
        rvWatchHostory.addItemDecoration(new DividerGridItemDecoration(this));
        mAdapter.setOnItemClickListener(this);
        prrvWatchHistory.setMode(PullToRefreshBase.Mode.BOTH);
        prrvWatchHistory.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                page = 1;
                loadWatchHistory();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                if (isLast == 1) {
                    prrvWatchHistory.onRefreshComplete();
                    showToast("已经到底啦");
                } else {
                    page++;
                    loadWatchHistory();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });
    }

    private void createCleanCacheDialog() {
        final Dialog cleanCacheDialog = new Dialog(this);
        Window window = cleanCacheDialog.getWindow();
        window.setContentView(R.layout.layout_choose_dialog);
        cleanCacheDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.TOP);
        cleanCacheDialog.setCanceledOnTouchOutside(false);
        Button btnCancel = (Button) cleanCacheDialog.findViewById(R.id.btnCancel);
        Button btnSure = (Button) cleanCacheDialog.findViewById(R.id.btnSure);
        TextView tvContent = (TextView) cleanCacheDialog.findViewById(R.id.tvContent);
        tvContent.setText("清空后将无法恢复，是否要清空");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanCacheDialog.dismiss();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                params.clear();
                User user = UserProxy.getUser();
                if (user != null) {
                    params.put("userId", user.getId());
                    params.put("token", user.getToken());
                    sendHttpRequest(API.CLEAR_HISTORY, params);
                    showLoading("");
                }
                cleanCacheDialog.dismiss();
            }
        });
        cleanCacheDialog.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = cleanCacheDialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); //设置宽度
        cleanCacheDialog.getWindow().setAttributes(lp);
    }


    private void loadWatchHistory() {
        User user = UserProxy.getUser();
        params.clear();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpRequest(API.WATCH_HISTORY, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prrvWatchHistory.onRefreshComplete();
        try {
            Gson gson = new Gson();
            switch (method) {
                case API.WATCH_HISTORY:
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
                    break;
                case API.CLEAR_HISTORY:
                    page = 1;
                    mDatas.clear();
                    mAdapter.notifyDataSetChanged();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mDatas.get(position) != null) {
            Intent intent = new Intent();
            intent.setClass(WatchHistoryActivity.this, LiveRoomActivity.class);
            intent.putExtra("id", mDatas.get(position).getRoom()+"");
            startActivity(intent);
        }
    }
}
