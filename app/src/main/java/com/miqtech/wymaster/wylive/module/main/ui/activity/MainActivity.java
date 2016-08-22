package com.miqtech.wymaster.wylive.module.main.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.main.ui.fragment.FragmentAttention;
import com.miqtech.wymaster.wylive.module.main.ui.fragment.FragmentLiveCategory;
import com.miqtech.wymaster.wylive.module.screenrecorder.ui.ScreenRecorderActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

import butterknife.OnClick;


@LayoutId(R.layout.activity_main)
public class MainActivity extends BaseAppCompatActivity {

    @BindView(R.id.ll_main_bar)
    LinearLayout llMainBar;



    @BindView(R.id.img)
    ImageView img;

    private int[] barSelected = new int[]{R.drawable.icon_bar_main_selected, R.drawable.icon_bar_category_selected,
            R.drawable.icon_bar_attention_selected, R.drawable.icon_bar_mine_selected};
    private int[] barUnselected = new int[]{R.drawable.icon_bar_main_unselected,
            R.drawable.icon_bar_category_unselected, R.drawable.icon_bar_attention_unselected, R.drawable.icon_bar_mine_unselected};

    private Class[] classes = {FragmentLiveCategory.class, FragmentLiveCategory.class, FragmentAttention.class, FragmentLiveCategory.class};
    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this instanceof MainActivity) {
            NBSAppAgent.setLicenseKey(Constants.TINGYUN_APP_KEY).withLocationServiceEnabled(true).start(this.getApplicationContext());
        }
    }*/

    private List<Fragment> fragmentList;
    int mSelected = 0;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        fragmentList = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            fragmentList.add(null);
        }

        User user = UserProxy.getUser();

        L.e(TAG, "---user-- null " + String.valueOf(user == null));
        if (user != null)
            L.e(TAG, "---user-- null " + user.toString());
        HashMap params = new HashMap();
        params.put("username", "13032111821");
        params.put("password", "12345678");
        sendHttpRequest(API.LOGIN, params);

        AsyncImage.displayImage("uploads/2016/07/26/35e53c1d42504def8144860732c7a010.jpg", img);


    }

    @OnClick({ R.id.tv_main_bar_match, R.id.tv_main_bar_information, R.id.tv_main_bar_find, R.id.tv_main_bar_mine})
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
                L.e(TAG, "---jump---11---");
                jumpToActivity(ScreenRecorderActivity.class);
                L.e(TAG, "---jump----22--");
                break;
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
        showErrorView(false);
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
}
