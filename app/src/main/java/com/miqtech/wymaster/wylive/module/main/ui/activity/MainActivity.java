package com.miqtech.wymaster.wylive.module.main.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.main.ui.fragment.FragmentAttention;
import com.miqtech.wymaster.wylive.module.main.ui.fragment.FragmentHallCategory;
import com.miqtech.wymaster.wylive.module.main.ui.fragment.FragmentLiveCategory;
import com.miqtech.wymaster.wylive.module.main.ui.fragment.FragmentMine;
import com.miqtech.wymaster.wylive.module.screenrecorder.ui.ScreenRecorderActivity;

import com.miqtech.wymaster.wylive.module.search.ui.activity.SearchActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import org.feezu.liuli.timeselector.Utils.DateUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;


@LayoutId(R.layout.activity_main)
public class MainActivity extends BaseAppCompatActivity {
    private long lastClickBackTime = -1;
    private static final int QUIT_CONFIRM_TIME = 3000;

    @BindView(R.id.ll_main_bar)
    LinearLayout llMainBar;
    private int[] barSelected = new int[]{R.drawable.icon_bar_main_selected, R.drawable.icon_bar_category_selected,
            R.drawable.icon_bar_attention_selected, R.drawable.icon_bar_mine_selected};
    private int[] barUnselected = new int[]{R.drawable.icon_bar_main_unselected,
            R.drawable.icon_bar_category_unselected, R.drawable.icon_bar_attention_unselected, R.drawable.icon_bar_mine_unselected};

    private Class[] classes = {FragmentHallCategory.class, FragmentLiveCategory.class, FragmentAttention.class, FragmentMine.class};
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof MainActivity) {
            NBSAppAgent.setLicenseKey(Constants.TINGYUN_APP_KEY).withLocationServiceEnabled(true).start(this.getApplicationContext());
        }
    }*/

    private List<Fragment> fragmentList;
    int mSelected = 0;
    private int registerType;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            fragmentList.add(null);
        }

        setSelectItem(0);
//        registerType = getIntent().getIntExtra("registerType", -1);
//        if(registerType == 1){
//            setSelectItem(3);
//        }else{
//            setSelectItem(0);
//        }
    }

    @OnClick({R.id.tv_main_bar_match, R.id.tv_main_bar_information, R.id.tv_main_bar_find, R.id.tv_main_bar_mine})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCurrentCity:
                break;
            case R.id.iv_scan:
                break;
            case R.id.iv_search:
                break;
            case R.id.tv_main_bar_match:
                setSelectItem(0);
                break;
            case R.id.tv_main_bar_information:
                setSelectItem(1);
                break;
            case R.id.tv_main_bar_find:
                setSelectItem(2);
                break;
            case R.id.tv_main_bar_mine:
                setSelectItem(3);
                break;
        }
    }

    private void changeLableState(int index) {
        int childCount = llMainBar.getChildCount();
        for (int i = 0; i < childCount; i++) {
            TextView child = (TextView) llMainBar.getChildAt(i);
            Drawable icon = getResources().getDrawable(barUnselected[i]);
            int textColor = getResources().getColor(R.color.bar_text_unselected);
            if (index == i) {
                icon = getResources().getDrawable(barSelected[i]);
                textColor = getResources().getColor(R.color.bar_text_selected);
            }
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
            child.setCompoundDrawables(null, icon, null, null);
            child.setTextColor(textColor);
        }
    }

    public void setSelectItem(int position) {
        mSelected = position;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        //先隐藏所有fragment
        for (Fragment fragment : fragmentList) {
            if (null != fragment) {
                fragmentTransaction.hide(fragment);
            }
        }
        Fragment fragment;
        if (null == fragmentList.get(position)) {
            Bundle bundle = new Bundle();
            // bundle.putString(Constant.TITLE, drawerTitles[position]);
            fragment = Fragment.instantiate(this, classes[position].getName(), bundle);
            fragmentList.set(position, fragment);
            // 如果Fragment为空，则创建一个并添加到界面上
            fragmentTransaction.add(R.id.fragment_content, fragment);
        } else {
            // 如果Fragment不为空，则直接将它显示出来
            fragment = fragmentList.get(position);
            fragmentTransaction.show(fragment);
        }
        fragmentTransaction.commitAllowingStateLoss();
        changeLableState(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UserProxy.removeAllListener();
    }

    @Override
    public void onBackPressed() {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastClickBackTime) >= QUIT_CONFIRM_TIME) {
            lastClickBackTime = curTime;
            showToast("再按一次退出");
        } else {
            finish();
        }
    }
}
