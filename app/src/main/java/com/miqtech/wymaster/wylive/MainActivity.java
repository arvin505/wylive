package com.miqtech.wymaster.wylive;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.screenrecorder.ui.ScreenRecorderActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;

import butterknife.OnClick;


@LayoutId(R.layout.activity_main)
public class MainActivity extends BaseAppCompatActivity  {

    @BindView(R.id.ll_main_bar)
    LinearLayout llMainBar;
    @BindView(R.id.tvCurrentCity)
    TextView tvCurrentCity;
    @BindView(R.id.tv_main_bar_match)
    TextView tvMainBarMatch;
    @BindView(R.id.tv_main_bar_information)
    TextView tvMainBarInformation;
    @BindView(R.id.tv_main_bar_find)
    TextView tvMainBarFind;
    @BindView(R.id.tv_main_bar_mine)
    TextView tvMainBarMine;

    @BindView(R.id.img)
    ImageView img;

    private int[] barSelected = new int[]{R.drawable.icon_bar_match_selected, R.drawable.icon_bar_info_selected,
            R.drawable.icon_bar_find_selected, R.drawable.icon_bar_mine_selected};
    private int[] barUnselected = new int[]{R.drawable.icon_bar_match_unselected,
            R.drawable.icon_bar_info_unselected, R.drawable.icon_bar_find_unselected, R.drawable.icon_bar_mine_unselected};


    @Override
    protected void initViews(Bundle savedInstanceState) {

        User user = UserProxy.getUser();

        L.e(TAG,"---user-- null " + String.valueOf(user == null));
        if (user!=null)
        L.e(TAG,"---user-- null " + user.toString());
        HashMap params = new HashMap();
        params.put("username", "13032111821");
        params.put("password", "12345678");
        sendHttpRequest(API.LOGIN, params);

        AsyncImage.displayImage("uploads/2016/07/26/35e53c1d42504def8144860732c7a010.jpg",img);
    }




    @OnClick({R.id.tvCurrentCity, R.id.iv_scan, R.id.iv_search, R.id.tv_main_bar_match, R.id.tv_main_bar_information, R.id.tv_main_bar_find, R.id.tv_main_bar_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCurrentCity:
                break;
            case R.id.iv_scan:
                break;
            case R.id.iv_search:
                break;
            case R.id.tv_main_bar_match:
                changeLableState(0);
                break;
            case R.id.tv_main_bar_information:
                changeLableState(1);
                break;
            case R.id.tv_main_bar_find:
                changeLableState(2);
                break;
            case R.id.tv_main_bar_mine:
                changeLableState(3);
                L.e(TAG,"---jump---11---");
                jumpToActivity(ScreenRecorderActivity.class);
                L.e(TAG,"---jump----22--");
                break;
        }
    }

    /**
     * 修改底部bar状态
     *
     * @param index
     */
    private void changeLableState(int index) {
        int childCount = llMainBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView child = (TextView) llMainBar.getChildAt(i);
            Drawable icon = getResources().getDrawable(barUnselected[i]);
            int textColor = getResources().getColor(R.color.textColorGray);
            if (index == i) {
                icon = getResources().getDrawable(barSelected[i]);
                textColor = getResources().getColor(R.color.colorActionBarSelected);
            }
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
            child.setCompoundDrawables(null, icon, null, null);
            child.setTextColor(textColor);
        }

    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        Gson gson = new Gson();
        try {
            User user = gson.fromJson(object.getJSONObject("object").toString(), User.class);
            L.e(TAG, "--------------   " + user.toString());
            UserProxy.setUser(user);
            User user2 = UserProxy.getUser();
            L.e(TAG, "--------user2------   " + user2.toString());
            UserProxy.setUser(user);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
