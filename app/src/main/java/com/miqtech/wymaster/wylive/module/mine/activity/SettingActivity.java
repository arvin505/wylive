package com.miqtech.wymaster.wylive.module.mine.activity;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miqtech.wymaster.wylive.BuildConfig;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.entity.VersionInfo;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.service.DownloadService;
import com.miqtech.wymaster.wylive.utils.FileSizeUtil;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.widget.CustomDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;

/**
 * Created by wuxn on 2016/8/29.
 */
@LayoutId(R.layout.activity_setting)
@Title(title = "设置")
public class SettingActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.ivSwitch)
    ImageView ivSwitch;
    @BindView(R.id.rlCleanCache)
    RelativeLayout rlCleanCache;
    @BindView(R.id.rlCheckVersion)
    RelativeLayout rlCheckVersion;
    @BindView(R.id.btnExit)
    Button btnExit;
    @BindView(R.id.tvCacheNum)
    TextView tvCacheNum;
    @BindView(R.id.tvVersion)
    TextView tvVersion;

    @BindView(R.id.rlHostSetting)
    RelativeLayout rlHostSetting;
    @BindView(R.id.spinner_url)
    Spinner spinnerUrl;

    private boolean isBinded;

    private DownloadService.DownloadBinder binder;

    private HashMap<String, String> params = new HashMap<>();


    @Override
    protected void initViews(Bundle savedInstanceState) {
        File cacheFile = ImageLoader.getInstance().getDiscCache().getDirectory();
        String cacheCount = FileSizeUtil.getAutoFileOrFilesSize(cacheFile);
        tvCacheNum.setText(cacheCount);
        rlCleanCache.setOnClickListener(this);
        rlCheckVersion.setOnClickListener(this);
        ivSwitch.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        ivSwitch.setTag("1");
        User user = UserProxy.getUser();
        if (user != null) {
            btnExit.setVisibility(View.VISIBLE);
        } else {
            btnExit.setVisibility(View.GONE);
        }

        try {
            String versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            tvVersion.setText("版本号  " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            rlHostSetting.setVisibility(View.VISIBLE);
            final String[] urls = getResources().getStringArray(R.array.host);
            spinnerUrl.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_host_simple, urls));
            spinnerUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    API.HOST = urls[position];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            int selection = 0;
            for (int i = 0; i < urls.length; i++) {
                if (urls[i].equals(API.HOST)) {
                    selection = i;
                }
            }
            spinnerUrl.setSelection(selection);
        } else {
            rlHostSetting.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        if (method.equals(API.CHECK_VERSION)) {
            initUpdate(object);
        }
    }


    private void initUpdate(JSONObject object) {
        if (object.has("object")) {
            try {
                String objectStr = object.getString("object");
                VersionInfo versionInfo = new Gson().fromJson(objectStr, VersionInfo.class);
                int localVersonCode = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
                if (versionInfo.getVersionCode() > localVersonCode) {
                    createUpdateDialog(versionInfo);
                } else {
                    showToast("当前已是最新版本");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void createUpdateDialog(final VersionInfo versionInfo) {
        L.e(TAG, "createUpdateDialog");
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_choose_dialog, null);
        final CustomDialog checkVersionDialog = new CustomDialog(this, contentView);
        checkVersionDialog.setCanceledOnTouchOutside(false);
        Button btnCancel = (Button) checkVersionDialog.findViewById(R.id.btnCancel);
        Button btnSure = (Button) checkVersionDialog.findViewById(R.id.btnSure);
        btnCancel.setText("取消");
        btnSure.setText("确定");
        TextView tvContent = (TextView) checkVersionDialog.findViewById(R.id.tvContent);
        tvContent.setText("检测到当前版本已更新，确认下载?");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersionDialog.dismiss();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersionDialog.dismiss();
                serviceStart(versionInfo);
            }
        });
        checkVersionDialog.show();
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBinded = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (DownloadService.DownloadBinder) service;
            System.out.println("服务启动!!!");
            // 开始下载
            isBinded = true;
        }
    };

    private void serviceStart(VersionInfo version) {
        Intent it = new Intent(SettingActivity.this, DownloadService.class);
        bindService(it, conn, Context.BIND_AUTO_CREATE);
        it.putExtra("apkUrl", version.getUrl());
        startService(it);
    }

    private void createCleanCacheDialog() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_choose_dialog, null);
        final CustomDialog dialog = new CustomDialog(this, contentView);
        Button btnCancel = (Button) contentView.findViewById(R.id.btnCancel);
        Button btnSure = (Button) contentView.findViewById(R.id.btnSure);
        TextView tvContent = (TextView) contentView.findViewById(R.id.tvContent);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageLoader.getInstance().getDiscCache().clear();
                File cacheFile = ImageLoader.getInstance().getDiscCache().getDirectory();
                String cacheCount = FileSizeUtil.getAutoFileOrFilesSize(cacheFile);
                tvCacheNum.setText(cacheCount);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlCleanCache:
                createCleanCacheDialog();
                break;
            case R.id.ivSwitch:
                if (ivSwitch.getTag().equals("1")) {
                    ivSwitch.setImageDrawable(getResources().getDrawable(R.drawable.open));
                    ivSwitch.setTag("0");
                } else if (ivSwitch.getTag().equals("0")) {
                    ivSwitch.setImageDrawable(getResources().getDrawable(R.drawable.off));
                    ivSwitch.setTag("1");
                }
                break;
            case R.id.btnExit:
                UserProxy.setUser(null);
                btnExit.setVisibility(View.GONE);
                showToast("退出成功");
                finish();
                break;
            case R.id.rlCheckVersion:
                checkVersion();
                break;
        }
    }

    private void checkVersion() {
        params.clear();
        params.put("systemType", 2 + "");
        sendHttpRequest(API.CHECK_VERSION, params);
    }

    private ICallbackResult callback = new ICallbackResult() {
        @Override
        public void OnBackResult(Object result) {
            if ("finish".equals(result)) {
                finish();
                return;
            }
            int i = (Integer) result;
        }
    };

    public interface ICallbackResult {
        public void OnBackResult(Object result);
    }
}
