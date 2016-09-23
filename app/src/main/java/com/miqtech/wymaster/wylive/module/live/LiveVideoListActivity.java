package com.miqtech.wymaster.wylive.module.live;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.VideoListInfo;
import com.miqtech.wymaster.wylive.module.live.adapter.HallAdapter;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshBase;
import com.miqtech.wymaster.wylive.widget.pullToRefresh.PullToRefreshRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by admin on 2016/7/28.
 */
@LayoutId(R.layout.acitivity_video_layout)
@Title(title="全部视频")
public class LiveVideoListActivity extends BaseAppCompatActivity implements View.OnClickListener ,Observerable.ISubscribe{
    @BindView(R.id.btnVideoHot)
    Button btnVideoHot; //最热视频
    @BindView(R.id.btnVideoNew)
    Button btnVideoNew ;//最新视频
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private int newPage = 1;
    private int hotPage=1;
    private int pageSize = 10;
    private int isNewLast=0;
    private int isHotLast=0;
    private LinearLayoutManager newlayoutManager;
    private LinearLayoutManager hotlayoutManager;
    private HallAdapter newAdapter;
    private HallAdapter hotAdapter;
    private List<LiveInfo> liveDatas=new ArrayList<LiveInfo>(); //直播列表数据
    private List<LiveInfo> newVideoDatas=new ArrayList<LiveInfo>();//最新视频数据
    private List<LiveInfo> hotVideoDatas=new ArrayList<LiveInfo>();//最热视频数据
    private VideoListInfo videoListInfo;
    private List<PullToRefreshRecyclerView> pages;
    private RecyclerView newRecyclerView; //最新视频RecyclerView
    private RecyclerView hotRecyclerView; //最热视屏RecyclerView
    private  PullToRefreshRecyclerView newVideoRV;
    private  PullToRefreshRecyclerView  hotVideoRV;
    private String[] titleStrings = new String[] { "最热视频", "最新视频" };
    private int type=0;//0 最新和最热视频都为空 1 最新为空 2 最热为空
    @Override
    protected void initViews(Bundle savedInstanceState) {
        initView();
        setOnClickListener();
        initData();
    }
    private void setOnClickListener() {
        btnVideoHot.setOnClickListener(this);
        btnVideoNew.setOnClickListener(this);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                changeState(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }
    protected void initView() {
        pages=new ArrayList<>();
         newVideoRV = (PullToRefreshRecyclerView) LayoutInflater.from(this).inflate(
                R.layout.live_play_listview, null);
        newRecyclerView=newVideoRV.getRefreshableView();
        hotVideoRV = (PullToRefreshRecyclerView) LayoutInflater.from(this).inflate(
                R.layout.live_play_listview, null);
        hotRecyclerView=hotVideoRV.getRefreshableView();
        pages.add(hotVideoRV);
        pages.add(newVideoRV);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public CharSequence getPageTitle(int position) {
                return titleStrings[position];
            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return pages.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                container.removeView(pages.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(pages.get(position));
                return pages.get(position);
            }
        });
        newlayoutManager = new LinearLayoutManager(this);
        hotlayoutManager= new LinearLayoutManager(this);

        newlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        hotlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        newRecyclerView.setLayoutManager(newlayoutManager);
        hotRecyclerView.setLayoutManager(hotlayoutManager);

        newVideoRV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //TODO 去分别下载数据
                type=1;
                newPage = 1;
                getInfomations(1+"",newPage);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                type=1;
                if (isNewLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            newPage++;
                            getInfomations(1+"",newPage);
                        }
                    }, 1000);
                } else {
                    showToast("已经到底部了");
                    newVideoRV.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });

        hotVideoRV.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                //TODO 去分别下载数据
                type=2;
                hotPage = 1;
                getInfomations(0+"",hotPage);

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                type=2;
                if (isHotLast == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            hotPage++;
                            getInfomations(0+"",hotPage);
                        }
                    }, 1000);
                } else {
                   showToast("已经到底部了");
                    hotVideoRV.onRefreshComplete();
                }
            }

            @Override
            public void isHasNetWork(boolean isHasNetWork) {
                if(!isHasNetWork){
                    showToast(getResources().getString(R.string.noNeteork));
                }
            }
        });
        changeState(0);
        Observerable.getInstance().subscribe(ObserverableType.VIDEO_TIMES,this);
    }

    private void changeState(int position) {
        viewPager.setCurrentItem(position, true);
        if (position == 0) {
            btnVideoHot.setBackgroundResource(R.drawable.bg_anchor_selected);
            btnVideoNew.setBackgroundResource(R.drawable.bg_game_unselected);
            btnVideoHot.setTextColor(this.getResources().getColor(R.color.white));
            btnVideoNew.setTextColor(this.getResources().getColor(R.color.bar_text_selected));
        } else {
            btnVideoHot.setBackgroundResource(R.drawable.bg_anchor_unselected);
            btnVideoNew.setBackgroundResource(R.drawable.bg_game_selected);
            btnVideoHot.setTextColor(this.getResources().getColor(R.color.bar_text_selected));
            btnVideoNew.setTextColor(this.getResources().getColor(R.color.white));
        }
    }
    protected void initData() {
        getInfomations("",1);
    }
    private void getInfomations(String type,int page){

       // showLoading();
        Map<String, String> params = new HashMap();
        params.put("page", page + "");
        params.put("pageSize", pageSize + "");
        //TODO 传递数据
        if(!TextUtils.isEmpty(type)) {
            params.put("type", type);
        }
        sendHttpRequest(API.VIDEO_LIST,params);
    }



    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
      //  hideLoading();
        newVideoRV.onRefreshComplete();
        hotVideoRV.onRefreshComplete();
        if (API.VIDEO_LIST.equals(method)) {
            try {
                if ("0".equals(object.getString("code")) && object.has("object")) {
                    Gson gs =new Gson();
                    videoListInfo = gs.fromJson(object.getJSONObject("object").toString(),VideoListInfo.class);
                    isHotLast = object.getJSONObject("object").getInt("isLast");
                    isNewLast = isHotLast;
                    if (videoListInfo != null) {
                        if (videoListInfo.getNewLive() != null) {
                            if (newPage == 1) {
                                newVideoDatas.clear();
                                newAdapter = new HallAdapter(this, 2, liveDatas, newVideoDatas);
                                newVideoDatas.addAll(videoListInfo.getNewLive());
                                newRecyclerView.setAdapter(newAdapter);
                            } else {
                                newVideoDatas.addAll(videoListInfo.getNewLive());
                                newAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(type!=2) {
                                setErrorView(1);
                            }
                        }
                        if (videoListInfo.getHotLive() != null) {
                            if (hotPage == 1) {
                                hotVideoDatas.clear();
                                hotAdapter = new HallAdapter(this, 2, liveDatas, hotVideoDatas);
                                hotVideoDatas.addAll(videoListInfo.getHotLive());
                                hotRecyclerView.setAdapter(hotAdapter);
                            } else {
                                hotVideoDatas.addAll(videoListInfo.getHotLive());
                                hotAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if(type!=1) {
                                setErrorView(2);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        newVideoRV.onRefreshComplete();
        hotVideoRV.onRefreshComplete();
      //  hideLoading();
        setErrorView(type);
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        newVideoRV.onRefreshComplete();
        hotVideoRV.onRefreshComplete();
      //  hideLoading();
        newAdapter.setNetWorkState(false);
        hotAdapter.setNetWorkState(false);
        setErrorView(0);
    }

    @Override
    protected void onDestroy() {
        Observerable.getInstance().unSubscribe(ObserverableType.VIDEO_TIMES,this);
        super.onDestroy();
    }

    /**
     * 设置没有数据的错误页面
     */
    private void setErrorView(int type){
        liveDatas.clear();
        if(type==0) {
            newVideoDatas.clear();
            newRecyclerView.setAdapter(newAdapter);
            hotVideoDatas.clear();
            hotRecyclerView.setAdapter(hotAdapter);
        }else if(type==1){
            newVideoDatas.clear();
            newRecyclerView.setAdapter(newAdapter);
        }else if(type==2){
            hotVideoDatas.clear();
            hotRecyclerView.setAdapter(hotAdapter);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVideoHot:
                changeState(0);
                type=1;
                break;
            case R.id.btnVideoNew:
                changeState(1);
                type=2;
                break;
        }
    }

    @Override
    public <T> void update(T... data) {
        int id = (Integer) data[0];
        for (int i = 0; i < newVideoDatas.size(); i++) {
            if (newVideoDatas.get(i).getId()==id) {
                newVideoDatas.get(i).setPlayTimes(newVideoDatas.get(i).getPlayTimes()+1);
                newAdapter.notifyDataSetChanged();
            }
        }
        for(int i=0; i< hotVideoDatas.size();i++){
            if(hotVideoDatas.get(i).getId()==id){
                hotVideoDatas.get(i).setPlayTimes(hotVideoDatas.get(i).getPlayTimes()+1);
                hotAdapter.notifyDataSetChanged();
            }
        }
    }
}
