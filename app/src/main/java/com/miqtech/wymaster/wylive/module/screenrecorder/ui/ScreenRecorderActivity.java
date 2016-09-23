package com.miqtech.wymaster.wylive.module.screenrecorder.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.SimpleTextWatcher;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.module.screenrecorder.Constants;
import com.miqtech.wymaster.wylive.module.screenrecorder.services.PiliPushService;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.PreferenceUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaoyi on 2016/7/27.
 * 录播activity
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@LayoutId(R.layout.activity_screenrecorder)
@Title(title = "直播设置")
public class ScreenRecorderActivity extends BaseAppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    public static int REQUEST_CODE_CAPTURE_PERM = 0x123;
    @BindView(R.id.et_live_title)
    EditText etLiveTitle;
    @BindView(R.id.et_live_content)
    EditText etLiveContent;
    @BindView(R.id.rb_orientation_horizontal)
    RadioButton rbOrientationHorizontal;
    @BindView(R.id.rb_orientation_vertical)
    RadioButton rbOrientationVertical;
    @BindView(R.id.rg_orientation)
    RadioGroup rgOrientation;
    @BindView(R.id.rb_quality_m)
    RadioButton rbQualityM;
    @BindView(R.id.rb_quality_h)
    RadioButton rbQualityH;
    @BindView(R.id.rb_quality_xh)
    RadioButton rbQualityXh;
    @BindView(R.id.rg_quality)
    RadioGroup rgQuality;
    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.img_clear)
    ImageView imgClear;
    @BindView(R.id.ll_root)
    LinearLayout llRoot;
    @BindView(R.id.ll_start)
    LinearLayout llStart;
    @BindView(R.id.img_start)
    ImageView imgStart;
    ProgressDialog progressDialog;


    private List<LiveTypeInfo> mLiveTypes;

    private PiliPushService mPiliPushService;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mDensityDpi;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private final static String SEPARATOR = "|";

    private int mOrientation = 1;
    private int mVideoWidth = 960;
    private int mVideoHeight = 544;
    private int mBitRate = 600;
    private String pushUrl = Constants.PUSH_URL_ANDROID_TEST_01;

    private PopupWindow popupWindow;


    @Override
    public void initViews(Bundle savedInstanceState) {
        getSize();
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        Intent bindIntent = new Intent(this, PiliPushService.class);
        startService(bindIntent);
        bindService(bindIntent, conn, BIND_AUTO_CREATE);
        initView();
        getGameList();
    }


    public void initView() {
        rgOrientation.check(R.id.rb_orientation_vertical);
        rgQuality.check(R.id.rb_quality_h);

        rgOrientation.setOnCheckedChangeListener(this);
        rgQuality.setOnCheckedChangeListener(this);
        etLiveTitle.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (etLiveTitle.getText().length() > 0) {
                    imgClear.setVisibility(View.VISIBLE);
                } else {
                    imgClear.setVisibility(View.GONE);
                }
            }
        });

    }

    /**
     * 屏幕尺寸
     */
    private void getSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mDensityDpi = metrics.densityDpi;
    }


    /**
     * 采用绑定服务的方式
     * 将录屏推流的工作放在服务中
     */
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPiliPushService = ((PiliPushService.PiliPushBinder) service).getService();
            if (mPiliPushService.isStreaming()) {
                btnStart.setText("停止直播");
                imgStart.setImageResource(R.drawable.icon_live_stop);
                String[] params = getLive();
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        L.e(TAG, "----split   " + params[i]);
                    }
                    etLiveTitle.setText(params[0]);
                    etLiveContent.setText(params[1]);
                    rgOrientation.check(Integer.parseInt(params[2]));
                    rgQuality.check(Integer.parseInt(params[3]));
                }
                setEnable(false);
            } else {
                btnStart.setText("开始直播");
                imgStart.setImageResource(R.drawable.icon_live_start);
                saveLive("");
                setEnable(true);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPiliPushService = null;
        }
    };


    @Override
    protected void onDestroy() {
        mMediaProjection = null;
        mPiliPushService.releaseActivity();
        unbindService(conn);
        super.onDestroy();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE_CAPTURE_PERM == requestCode) {
            if (resultCode == RESULT_OK && Build.VERSION.SDK_INT >= 21) {
                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
                mPiliPushService.startPushStream(mMediaProjection);
                btnStart.setText("停止直播");
                imgStart.setImageResource(R.drawable.icon_live_stop);
                saveLive(null);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        liveNotify();
                    }
                });
                setEnable(false);
            } else {
                setEnable(true);
                showToast("直播需要权限，请允许");
            }
        }
    }


    /**
     * 设置推流地址
     */
    private void setPushUrl(int position) {
        pushUrl = Constants.PUSH_URLS.get(Constants.PUSH_URL_KEYS.get(position));
    }

    /**
     * 设置质量参数
     */
    private void setQuality(int checkId) {
        if (checkId == R.id.rb_quality_xh) {
            mVideoWidth = 720;
            mVideoHeight = 1280;
            mBitRate = 1200;
        } else if (checkId == R.id.rb_quality_h) {
            mVideoWidth = 544;
            mVideoHeight = 960;
            mBitRate = 600;
        } else if (checkId == R.id.rb_quality_m) {
            mVideoWidth = 480;
            mVideoHeight = 848;
            mBitRate = 300;
        }
    }

    /**
     * 设置方向参数
     *
     * @param checkId
     */
    private void setOrientation(int checkId) {
        if (checkId == R.id.rb_orientation_horizontal) {
            mOrientation = 0;
        } else {
            mOrientation = 1;
        }
    }

    /**
     *
     */
    private void setSizeWithOrientation() {
        int tmpBig = Math.max(mVideoWidth, mVideoHeight);
        int tmpSmall = Math.min(mVideoWidth, mVideoHeight);
        if (mOrientation == 0) {
            mVideoWidth = tmpSmall;
            mVideoHeight = tmpBig;
        } else {
            mVideoWidth = tmpBig;
            mVideoHeight = tmpSmall;
        }
    }

    @OnClick({R.id.ibLeft, R.id.ll_start, R.id.img_clear, R.id.img_choose_game})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibLeft:
                onBackPressed();
                break;
            case R.id.ll_start:
                if (mPiliPushService.isStreaming()) {
                    btnStart.setText("开始直播");
                    imgStart.setImageResource(R.drawable.icon_live_start);
                    mPiliPushService.stopService();
                    setEnable(true);
                    closeLive();
                } else {
                    setSizeWithOrientation();
                    /*mPiliPushService.setParams(this, mVideoWidth, mVideoHeight, mDensityDpi, mOrientation, mBitRate, Constants.PUSH_URL_ANDROID_TEST_01);
                    mPiliPushService.startPushStream();
                    btnStart.setText("停止直播");*/
                    if (TextUtils.isEmpty(etLiveTitle.getText().toString().trim()) ||
                            TextUtils.isEmpty(etLiveContent.getText().toString().trim())) {
                        showToast("直播标题和内容不能为空");
                    } else {
                        requestLive();
                    }

                }


                break;
            case R.id.img_clear:
                etLiveTitle.setText("");
                break;
            case R.id.img_choose_game:
                showGamePopWindow();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.rg_orientation:
                setOrientation(checkedId);
                break;
            case R.id.rg_quality:
                setQuality(checkedId);
                break;
        }
    }

    /**
     * 直播内容列表获取
     */
    private void getGameList() {
        //showLoading();
        sendHttpRequest(API.GAMETYPE, null);
    }

    @Override
    public void onSuccess(JSONObject object, String method) {
        super.onSuccess(object, method);
        Gson gson = new Gson();
        try {
            switch (method) {
                case API.GAMETYPE:
                    mLiveTypes = gson.fromJson(object.getJSONArray("object").toString(),
                            new TypeToken<List<LiveTypeInfo>>() {
                            }.getType());
                    break;
                case API.LIVE_REQUEST:
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    pushUrl = object.getString("object");
                    mPiliPushService.setParams(this, mVideoWidth, mVideoHeight, mDensityDpi, mOrientation, mBitRate, pushUrl);
                    mPiliPushService.startPushStream();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showGamePopWindow() {
        if (popupWindow != null) {
            popupWindow.showAtLocation(llRoot, Gravity.TOP, 0, 0);
        } else {
            createGamePopWindow();
            popupWindow.showAtLocation(llRoot, Gravity.TOP, 0, 0);
        }
    }

    private void createGamePopWindow() {

        View convertView = LayoutInflater.from(this).inflate(R.layout.layout_pop_game, null, false);
        GridView gridView = (GridView) convertView.findViewById(R.id.gv_pop_game);
        GamePopAdapter adapter = new GamePopAdapter();
        gridView.setAdapter(adapter);
        popupWindow = new PopupWindow(convertView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
        popupWindow.setOutsideTouchable(true);

        convertView.findViewById(R.id.view_hidden).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        final EditText etPopContent = (EditText) convertView.findViewById(R.id.et_pop_content);
        Button btnSure = (Button) convertView.findViewById(R.id.btn_pop_sure);
        btnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLiveContent.setText(etPopContent.getText().toString());
                popupWindow.dismiss();
            }
        });
        popupWindow.setAnimationStyle(R.style.anim_pop_gametype);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etLiveContent.setText(mLiveTypes.get(position).getName());
                popupWindow.dismiss();
            }
        });
    }

    private class GamePopAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mLiveTypes == null) return 0;
            return mLiveTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return mLiveTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(ScreenRecorderActivity.this).inflate(R.layout.layout_pop_game_item, parent, false);
            }
            ((TextView) convertView).setText(mLiveTypes.get(position).getName());
            return convertView;
        }
    }

    /**
     * 通知后台设置参数
     */
    private void liveNotify() {
        User user = UserProxy.getUser();
        if (user != null) {
            Map<String, String> params = new HashMap<>();
            params.put("gameId", getGameId(etLiveContent.getText().toString()));
            params.put("title", etLiveTitle.getText().toString());
            if (mOrientation == 0) {
                params.put("screen", "1");
            } else {
                params.put("screen", "0");
            }
            params.put("token", user.getToken());
            params.put("userId", user.getId());
            params.put("source", "1");
            sendHttpRequest(API.LIVE_NOTIFY, params);
        }
    }

    private String getGameId(String name) {
        if (mLiveTypes == null || mLiveTypes.isEmpty()) {
            return name;
        }
        for (LiveTypeInfo liveTypeInfo : mLiveTypes) {
            if (liveTypeInfo.getName().equals(name)) {
                return liveTypeInfo.getId();
            }
        }
        return name;
    }

    /**
     * 请求直播
     */
    private void requestLive() {
        User user = UserProxy.getUser();
        if (user != null) {
            Map<String, String> params = new HashMap<>();
            params.put("token", user.getToken());
            params.put("userId", user.getId());
            sendHttpRequest(API.LIVE_REQUEST, params);
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("连接中...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

    }


    private void saveLive(String str) {
        /*if (str != null) {
            PreferencesUtil.saveLive("");
            return;
        }*/
        StringBuilder sb = new StringBuilder();
        sb.append(etLiveTitle.getText().toString())
                .append(SEPARATOR)
                .append(etLiveContent.getText().toString())
                .append(SEPARATOR)
                .append(rgOrientation.getCheckedRadioButtonId())
                .append(SEPARATOR)
                .append(rgQuality.getCheckedRadioButtonId());
        PreferenceUtils.putString("liveparams", sb.toString());

    }

    private String[] getLive() {
        String save = PreferenceUtils.getString("liveparams");
        if (save != null && !save.equals("")) {
            L.e(TAG, "----split ssss  " + save);
            String[] result = save.split("\\|");
            L.e(TAG, "----split result  " + result);
            return result;
        }
        return null;
    }

    @Override
    public void onFaild(JSONObject object, String method) {
        super.onFaild(object, method);
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onError(String errMsg, String method) {
        super.onError(errMsg, method);
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        showToast("网络错误");
    }

    /**
     * 设置直播状态下不可编辑
     */
    private void setEnable(boolean enable) {
        etLiveContent.setEnabled(enable);
        etLiveTitle.setEnabled(enable);
        imgClear.setEnabled(enable);
        /*setRadioGroupEnable(rgOrientation, enable);
        setRadioGroupEnable(rgQuality, enable);*/
        rgOrientation.setEnabled(enable);
        rgQuality.setEnabled(enable);
    }

    private void setRadioGroupEnable(RadioGroup group, boolean enable) {
        for (int i = 0; i < group.getChildCount(); i++) {
            RadioButton child = (RadioButton) group.getChildAt(i);
            child.setEnabled(enable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPiliPushService != null) {
            mPiliPushService.setShouldJump(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPiliPushService != null) {
            mPiliPushService.setShouldJump(true);
        }
    }

    private void closeLive() {
        User user = UserProxy.getUser();
        if (user != null) {
            Map<String, String> params = new HashMap<>();
            params.put("token", user.getToken());
            params.put("userId", user.getId());
            sendHttpRequest(API.CLOSE_LIVE, params);
        }
    }
}
