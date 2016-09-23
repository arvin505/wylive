package com.miqtech.wymaster.wylive.module.live.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveRoomAnchorInfo;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.observer.Observerable;
import com.miqtech.wymaster.wylive.observer.ObserverableType;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;


/**
 * Created by admin on 2016/7/26.
 */
@LayoutId(R.layout.fragment_information_lp)
public class FragmentInformationLP extends BaseFragment implements View.OnClickListener{

    @BindView(R.id.anchorHeader)
    CircleImageView anchorHeader;//主播头像
    @BindView(R.id.anchorTitle)
    TextView anchorTitle;  //主播标题
    @BindView(R.id.fansNum)
    TextView fansNum ;// 粉丝数量
    @BindView(R.id.tvAttention)
    TextView tvAttention; //关注按钮
    @BindView(R.id.anchorContent)
    TextView anchorContent; //主播内容
    @BindView(R.id.anchorSex)
    ImageView anchorSex;
    private String id;//房间id
    private int upId;//主播id
    private LiveRoomAnchorInfo info;//主播信息
    @Override
    protected void initViews(View view, Bundle savedInstanceState) {

    }
    public void setAnchorData(LiveRoomAnchorInfo info){
        setData(info);
    }
    private void setData(LiveRoomAnchorInfo info) {
        if(info==null){
            return;
        }
        this.info=info;
        this.id=info.getId();
        this.upId=info.getUpUserId();
        AsyncImage.displayImage(info.getIcon(), anchorHeader, R.drawable.default_head);
        anchorTitle.setText(info.getNickname());
        setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(),10000,"W")),2,getString(R.string.live_play_fans_num, Utils.calculate(info.getFans(),10000,"W")).length(),fansNum);
        if(!TextUtils.isEmpty(info.getIntroduce())){
            anchorContent.setText(info.getIntroduce());
        }else {
            anchorContent.setVisibility(View.GONE);
        }
        setSubscribeState(info.getIsSubscibe()==1?true:false);
        anchorSex.setVisibility(View.VISIBLE);
        if (info.getSex() == 0) {
            anchorSex.setImageResource(R.drawable.live_play_men);
        } else {
            anchorSex.setImageResource(R.drawable.live_play_femen);
        }
        tvAttention.setOnClickListener(this);


    }
    public void setSubscribeState(boolean isSubscribe){
        if (isSubscribe) {
            tvAttention.setText(mActivity.getResources().getString(R.string.live_room_attentioned));
            tvAttention.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            tvAttention.setTextColor(mActivity.getResources().getColor(R.color.search_edit_border));
            Utils.setShapeBackground(tvAttention,0,0,R.color.anchor_state_offline, DeviceUtils.dp2px(mActivity,5));
        } else {
            tvAttention.setText(mActivity.getResources().getString(R.string.live_room_attention));
            tvAttention.setTextColor(mActivity.getResources().getColor(R.color.white));
            tvAttention.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_attention,0,0,0);
            Utils.setShapeBackground(tvAttention,0,0,R.color.bar_text_selected, DeviceUtils.dp2px(mActivity,5));
        }
    }
    public void setSubscribeNum(boolean isSubscribe){
        if(isSubscribe) {
            setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans()+1, 10000, "W")), 2, getString(R.string.live_play_fans_num, Utils.calculate(info.getFans()+1, 10000, "W")).length(), fansNum);
            info.setFans(info.getFans()+1);
        }else{
            setFontDiffrentColor(getString(R.string.live_play_fans_num, Utils.calculate(info.getFans()-1, 10000, "W")), 2, getString(R.string.live_play_fans_num, Utils.calculate(info.getFans()-1, 10000, "W")).length(), fansNum);
            info.setFans(info.getFans()-1);
        }
    }
    private void setFontDiffrentColor(String content, int start, int end, TextView tv) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(this.getResources().getColor(R.color.bar_text_selected)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvAttention:
                getAttentionRequest();
                break;
        }
    }
    private void getAttentionRequest(){
       // showLoading();
        Map<String, String> params = new HashMap();
        if(UserProxy.getUser()!=null){
            params.put("userId",UserProxy.getUser().getId()+"");
            params.put("token",UserProxy.getUser().getToken());
        }else{
           jumpToActivity(LoginActivity.class);
        }
        params.put("upUserId",upId+"");
        //TODO 传递数据
        sendHttpRequest(API.LIVE_SUBSCRIBE,params);
        L.e(TAG,"开始请求数据");
    }


    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        L.e(TAG,"onSuccess");
      //  hideLoading();
        if (API.LIVE_SUBSCRIBE.equals(method)) {
            try {
                if(object.getInt("code")==0 && "success".equals(object.getString("result"))){
                    L.e(TAG,"onSuccess22222");
                    info.setIsSubscibe(info.getIsSubscibe()==1?0:1);
                    ((LiveRoomActivity)getActivity()).updataSubscribeState(info.getIsSubscibe()==1?true:false);
                    AnchorInfo anchorInfo = new AnchorInfo();
                    anchorInfo.setState(info.getState());
                    anchorInfo.setFans(info.getFans());
                    anchorInfo.setRoom(info.getRoom());
                    anchorInfo.setId(info.getId());
                    anchorInfo.setName(info.getNickname());
                    anchorInfo.setIcon(info.getIcon());
                    Observerable.getInstance().notifyChange(ObserverableType.ATTENTION_ANCHOR,anchorInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        L.e(TAG,"onFaild");
      //  hideLoading();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        L.e(TAG,"onError");
      //  hideLoading();
    }
}
