package com.miqtech.wymaster.wylive.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.live.PlayVideoActivity;
import com.miqtech.wymaster.wylive.module.live.adapter.HallAdapter;
import com.miqtech.wymaster.wylive.module.mine.adapter.MyVideoListAdapter;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

/**
 * 我的视频
 * Created by wuxn on 2016/8/25.
 */
@LayoutId(R.layout.activity_myvideo)
public class MyVideoActivity extends BaseAppCompatActivity implements AdapterView.OnItemClickListener ,Observerable.ISubscribe{
    @BindView(R.id.prlvMyVideo)
    PullToRefreshListView prlvMyVideo;

    private Context context;

    private MyVideoListAdapter adapter;

    private ListView lvMyVideo;

    private List<LiveInfo> liveInfos = new ArrayList<>();

    private HashMap<String, String> params = new HashMap<>();

    int page = 1;

    int pageSize = 10;
    //	是否是最后一页1是0否
    private int isLast;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        context = this;
        setTitle("我的视频");
        prlvMyVideo.setMode(PullToRefreshBase.Mode.BOTH);
        lvMyVideo = prlvMyVideo.getRefreshableView();
        adapter = new MyVideoListAdapter(context, liveInfos);
        lvMyVideo.setAdapter(adapter);
        lvMyVideo.setOnItemClickListener(this);
        Observerable.getInstance().subscribe(ObserverableType.VIDEO_TIMES,this);
        loadMyVideoInfo();
        prlvMyVideo.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = 1;
                loadMyVideoInfo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (isLast == 1) {
                    prlvMyVideo.onRefreshComplete();
                    showToast("已经到底啦");
                } else {
                    page++;
                    loadMyVideoInfo();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {

            }
        });
    }

    private void loadMyVideoInfo() {
        params.clear();
        User user = UserProxy.getUser();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpRequest(API.MY_VIDEO, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        prlvMyVideo.onRefreshComplete();
        try {
            if (object.has("object")) {
                String strObj = object.getString("object");
                JSONObject jsonObj = new JSONObject(strObj);
                isLast = jsonObj.getInt("isLast");
                String listStr = jsonObj.getString("list");
                List<LiveInfo> newLiveInfos = new Gson().fromJson(listStr, new TypeToken<List<LiveInfo>>() {
                }.getType());
                if (page == 1) {
                    liveInfos.clear();
                }
                liveInfos.addAll(newLiveInfos);
                adapter.notifyDataSetChanged();

                if (newLiveInfos == null || newLiveInfos.isEmpty()) {
                    if (page > 1) {
                        page--;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        prlvMyVideo.onRefreshComplete();
        try {
            showToast(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        prlvMyVideo.onRefreshComplete();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (liveInfos.get(position) != null) {
            Intent intent = new Intent();
            intent.setClass(context, PlayVideoActivity.class);
            intent.putExtra("videoId", liveInfos.get(position).getId()+"");
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        Observerable.getInstance().unSubscribe(ObserverableType.VIDEO_TIMES,this);
        super.onDestroy();
    }

    @Override
    public <T> void update(T... data) {
        int id = (Integer) data[0];
        for (int i = 0; i < liveInfos.size(); i++) {
            if (liveInfos.get(i).getId()==id) {
                liveInfos.get(i).setPlayTimes(liveInfos.get(i).getPlayTimes()+1);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
