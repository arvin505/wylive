package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.login.LoginActivity;
import com.miqtech.wymaster.wylive.module.mine.activity.EditUserInfoActviity;
import com.miqtech.wymaster.wylive.module.mine.activity.MyVideoActivity;
import com.miqtech.wymaster.wylive.module.mine.activity.SettingActivity;
import com.miqtech.wymaster.wylive.module.mine.activity.WatchHistoryActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by wuxn on 2016/8/22.
 */
@LayoutId(R.layout.fragment_mine)
@Title(title = "我的")
public class FragmentMine extends BaseFragment implements View.OnClickListener, UserProxy.OnUserChangeListener {
    @BindView(R.id.ivHeader)
    ImageView ivHeader;
    @BindView(R.id.tvUserName)
    TextView tvUserName;
    @BindView(R.id.tvLiveTime)
    TextView tvLiveTime;
    @BindView(R.id.tvMyRoom)
    TextView tvMyRoom;
    @BindView(R.id.tvEditInfo)
    TextView tvEditInfo;
    @BindView(R.id.rlMyFans)
    RelativeLayout rlMyFans;
    @BindView(R.id.rlHistory)
    RelativeLayout rlHistory;
    @BindView(R.id.rlMyVideo)
    RelativeLayout rlMyVideo;
    @BindView(R.id.rlSetting)
    RelativeLayout rlSetting;
    @BindView(R.id.tvFansNum)
    TextView tvFansNum;
    @BindView(R.id.ivSex)
    ImageView ivSex;
    @BindView(R.id.ivAuthentication)
    ImageView ivAuthentication;

    private Context context;


    private HashMap<String, String> params = new HashMap<>();
    //主播信息
    private AnchorInfo anchorInfo;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        ivHeader.setOnClickListener(this);
        tvMyRoom.setOnClickListener(this);
        tvEditInfo.setOnClickListener(this);
        rlMyFans.setOnClickListener(this);
        rlHistory.setOnClickListener(this);
        rlMyVideo.setOnClickListener(this);
        rlSetting.setOnClickListener(this);
        context = getActivity();
        UserProxy.addListener(this);
        User user = UserProxy.getUser();
        updateUserViews(user);
    }

    private void updateUserViews(User user) {
        if (user == null) {
            tvUserName.setText("亲！还没登录哦");
            tvLiveTime.setText("");
            tvMyRoom.setVisibility(View.GONE);
            tvFansNum.setText("");
            tvEditInfo.setText("马上登录");
            rlMyVideo.setVisibility(View.GONE);
            ivSex.setImageBitmap(null);
            ivAuthentication.setVisibility(View.GONE);
            ivHeader.setImageDrawable(getResources().getDrawable(R.drawable.logo_icon));
        } else {
            //性别0男1女
            if (user.getSex() == 0) {
                ivSex.setImageDrawable(getResources().getDrawable(R.drawable.home_male));
            } else if (user.getSex() == 1) {
                ivSex.setImageDrawable(getResources().getDrawable(R.drawable.home_female));
            }
            if (user.getFans() != 0) {
                tvFansNum.setText(user.getFans() + "");
            } else {
                tvFansNum.setText("你还没有粉丝哦");
            }
            if (user.getIsUp() == 0) {
                //不是主播
                tvUserName.setText(user.getNickname());
                tvEditInfo.setText("编辑信息");
                tvMyRoom.setVisibility(View.GONE);
                rlMyVideo.setVisibility(View.GONE);
                tvLiveTime.setVisibility(View.GONE);
                ivAuthentication.setVisibility(View.GONE);
                AsyncImage.displayImage(API.IMAGE_HOST + user.getIcon(), ivHeader);
            } else if (user.getIsUp() == 1) {
                //主播
                loadMineInfo(user);
                ivAuthentication.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadMineInfo(User user) {
        params.clear();
        params.put("userId", user.getId());
        params.put("token", user.getToken());
        sendHttpRequest(API.MINE, params);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        try {
            String strObj = object.getString("object");
            anchorInfo = new Gson().fromJson(strObj, AnchorInfo.class);
            tvUserName.setText(anchorInfo.getName());
            AsyncImage.displayImage(API.IMAGE_HOST + anchorInfo.getIcon(), ivHeader, R.drawable.default_head);
            tvEditInfo.setText("编辑信息");
            tvMyRoom.setVisibility(View.VISIBLE);
            rlMyVideo.setVisibility(View.VISIBLE);
            tvLiveTime.setText("已直播" + anchorInfo.getDuration() + "分钟");
            Utils.setFontDiffrentColor("已直播" + anchorInfo.getDuration() + "分钟",3,3+(anchorInfo.getDuration()+"").length(),tvLiveTime,R.color.attention_item_count);
            tvLiveTime.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        User user = UserProxy.getUser();
        switch (v.getId()) {
            case R.id.tvEditInfo:
                if (user == null) {
                    Intent intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), EditUserInfoActviity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rlMyVideo:
                if (user == null) {
                    Intent intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MyVideoActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rlHistory:
                if (user == null) {
                    Intent intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), WatchHistoryActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.rlSetting:
                Intent intent = new Intent();
                intent.setClass(context, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.tvMyRoom:
                if (user != null && anchorInfo != null && !TextUtils.isEmpty(anchorInfo.getLiveId())) {
                    intent = new Intent();
                    intent.setClass(context,LiveRoomActivity.class);
                    intent.putExtra("id",anchorInfo.getLiveId());
                    startActivity(intent);
                }else{
                   jumpToActivity(LoginActivity.class);
                }
                break;
        }
    }

    @Override
    public void onUserChange(User user) {
        updateUserViews(user);
    }

    @Override
    public void onDestroy() {
        UserProxy.removeListener(this);
        super.onDestroy();
    }
}
