package com.example.wydstv;

import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wydstv.core.ScreenRecorder;
import com.pili.pldroid.streaming.EncodingType;
import com.pili.pldroid.streaming.StreamingEnv;
import com.pili.pldroid.streaming.StreamingManager;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.StreamingState;
import com.pili.pldroid.streaming.StreamingStateListener;
import com.pili.pldroid.streaming.VideoSourceConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, StreamingStateListener, ScreenRecorder.ScreenRecorderListener {

    private final String TAG = "MainActivity";
    Spinner pushSpinner;
    Spinner qualitySpinner;

    Button btnStart;
    Button btnStop;
    TextView tvState;


    ArrayAdapter<String> adapter;
    ArrayAdapter<String> qualityAdapter;
    String pushUrl = Constants.PUSH_URL_ANDROID_TEST_01;
    MediaProjectionManager mMediaProjectionManaer;

    VideoSourceConfig mVideoSourceConfig;
    Button btnPushPrepare;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private static final int SAMPLE_RATE = 44100;
    JSONObject jsonObject;

    //push设置
    private int videoFrameRate = 30;
    private int videoBitRate = 1200 * 1024;

    private int mVideoWidth = 1280;
    private int mVideoHeight = 720;

    StreamingProfile.AVProfile avProfile;
    StreamingProfile.VideoProfile videoProfile;
    StreamingProfile.AudioProfile audioProfile;
    StreamingProfile.Stream stream;
    StreamingProfile streamingProfile;

    private StreamingManager mStreamingManager;
    private MediaProjection mMediaProjection;
    private ScreenRecorder mScreenRecorder;
    private static final int REQUEST_CODE_CAPTURE_PERM = 1234;

    private int videoQuility = StreamingProfile.VIDEO_QUALITY_HIGH1;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mMediaProjectionManaer = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        getScreenSize();
        initView();
    }


    private void initView() {
        pushSpinner = (Spinner) findViewById(R.id.spiner_push);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.PUSH_URL_KEYS);
        pushSpinner.setAdapter(adapter);
        pushSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pushUrl = Constants.PUSH_URLS.get(Constants.PUSH_URL_KEYS.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnPushPrepare = (Button) findViewById(R.id.btn_push_prepare);
        btnPushPrepare.setOnClickListener(this);

        qualitySpinner = (Spinner) findViewById(R.id.spinner_quality);
        qualityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constants.quality);
        qualitySpinner.setAdapter(qualityAdapter);
        qualitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO: 2016/7/21
               /* switch (position) {
                    case 0:
                        videoQuility = StreamingProfile.VIDEO_QUALITY_HIGH1;
                        videoFrameRate = 30;
                        videoBitRate = 1200 * 1024;
                        mVideoWidth = 1280;
                        mScreenHeight = 720;
                        break;
                    case 1:
                        videoQuility = StreamingProfile.VIDEO_QUALITY_MEDIUM1;
                        videoFrameRate = 30;
                        videoBitRate = 800 * 1024;
                        mVideoWidth = 720;
                        mScreenHeight = 576;
                        break;
                    case 2:
                        videoQuility = StreamingProfile.VIDEO_QUALITY_LOW2;
                        videoFrameRate = 15;
                        videoBitRate = 264 * 1024;
                        mVideoWidth = 640;
                        mScreenHeight = 480;
                        break;
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStop = (Button) findViewById(R.id.btn_stop);
        tvState = (TextView) findViewById(R.id.tv_state);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_push_prepare:
                preparePush();
                break;
            case R.id.btn_start:
                startPush();
                break;
            case R.id.btn_stop:
                stopPush();
                break;
        }
    }

    private void stopPush() {
        mStreamingManager.stopStreaming();
        mStreamingManager.pause();
        tvState.setText("已停止推流");
    }

    /**
     * 准备推送
     */
    private void preparePush() {
        try {
            jsonObject = new JSONObject(pushUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        audioProfile = new StreamingProfile.AudioProfile(SAMPLE_RATE, 48 * 1024);
        // TODO: 2016/7/21
        //videoProfile = new StreamingProfile.VideoProfile(videoFrameRate, videoBitRate, 30);
        StreamingProfile.VideoProfile videoProfile = new StreamingProfile.VideoProfile(25, 800 * 1024, 45);
        avProfile = new StreamingProfile.AVProfile(videoProfile, audioProfile);
        stream = new StreamingProfile.Stream(jsonObject);

        streamingProfile = new StreamingProfile();

        streamingProfile
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM1)
                .setStream(stream)
                .setAVProfile(avProfile)
                .setPreferredVideoEncodingSize(mScreenWidth , mScreenHeight )
                .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3);

        /*streamingProfile
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM1)
                .setStream(stream)
                .setAVProfile(avProfile)
                .setPreferredVideoEncodingSize(mScreenWidth/3, mScreenHeight/3)
                .setVideoQuality(videoQuility);
*/
        mVideoSourceConfig = new VideoSourceConfig();
        mVideoSourceConfig.setVideoSourceSize( mScreenHeight ,mScreenWidth );

        mStreamingManager = new StreamingManager(this, EncodingType.HW_SCREEN_VIDEO_WITH_HW_AUDIO_CODEC);
        mStreamingManager.prepare(streamingProfile);
        mStreamingManager.setStreamingStateListener(this);
        tvState.setText("准备推流");
    }

    private void startPush() {
        mStreamingManager.resume();
        tvState.setText("正在推流");
    }

    private void getScreenSize() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
       /* mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;*/
        mScreenWidth = (int) (720 );
        mScreenHeight = (int) (1280 );
        mScreenDensity = metrics.densityDpi;
        Log.e(TAG, "width ; " + metrics.widthPixels + " height : " + metrics.heightPixels + " density : " + mScreenDensity);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mScreenRecorder != null) {
            ScreenRecorder.mQuit.set(true);
            mScreenRecorder = null;
        }
        mStreamingManager.pause();
        mHandler.removeCallbacksAndMessages(null);
    }


    @Override
    public void frameAvailable(boolean endOfStream) {
        mStreamingManager.frameAvailable(endOfStream);
    }

    @Override
    public void sendAudioFrame(ByteBuffer buffer, int size, long ts, boolean isEof) {
        mStreamingManager.sendAudioFrame(buffer, size, ts, isEof);
    }

    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        Log.i(TAG, "streamingState:" + streamingState);
        switch (streamingState) {
            case READY:
                Intent permissionIntent = mMediaProjectionManaer.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_CODE_CAPTURE_PERM);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (REQUEST_CODE_CAPTURE_PERM == requestCode) {
            if (resultCode == RESULT_OK && Build.VERSION.SDK_INT >= 21) {


                mMediaProjection = mMediaProjectionManaer.getMediaProjection(resultCode, intent);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mStreamingManager.startStreaming(mVideoSourceConfig)) {
                            Surface inputSurface = mStreamingManager.getInputSurface();
                            if (inputSurface != null) {
                                mScreenRecorder = new ScreenRecorder(mMediaProjection, inputSurface,
                                        mScreenWidth , mScreenHeight , mScreenDensity, SAMPLE_RATE, MainActivity.this);
                                mScreenRecorder.start();
                            } else {
                                mStreamingManager.stopStreaming();
                            }
                        }
                    }
                });
            }
        }
    }
}
