package com.miqtech.wymaster.wylive.module;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;

/**
 * Created by zhaosentao on 2016/8/22.
 */
@LayoutId(R.layout.activity_login)
public class LoginActivity extends BaseAppCompatActivity {


    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
}
