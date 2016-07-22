package com.example.wydstv;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wydstv.core.ScreenRecorder;
import com.pili.pldroid.streaming.EncodingType;
import com.pili.pldroid.streaming.StreamingManager;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.StreamingState;
import com.pili.pldroid.streaming.StreamingStateListener;
import com.pili.pldroid.streaming.VideoSourceConfig;


import org.json.JSONObject;

import java.nio.ByteBuffer;

/**
 * Created by jerikc on 16/6/21.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenRecorderActivity extends Activity implements
        StreamingStateListener,
        ScreenRecorder.ScreenRecorderListener {

    private static final String TAG = "ScreenRecorderActivity";

    private MediaProjectionManager mMediaProjectionManager;


    private StreamingManager mStreamingManager;
    private MediaProjection mMediaProjection;
    private ScreenRecorder mScreenRecorder;
    private static final int REQUEST_CODE_CAPTURE_PERM = 1234;

    private VideoSourceConfig mVideoSourceConfig;
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;

    private TextView mTimeTv;

    private static final int SAMPLE_RATE = 44100;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mUpdateTimeTvRunnable = new Runnable() {
        @Override
        public void run() {
            mTimeTv.setText("currentTimeMillis:" + System.currentTimeMillis());
            mHandler.postDelayed(mUpdateTimeTvRunnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.screen_recoder);

        mTimeTv = (TextView) findViewById(R.id.time_tv);
        mHandler.post(mUpdateTimeTvRunnable);




        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(Constants.PUSH_URL_ANDROID_TEST_01);
        } catch (Exception e) {
            Log.e(TAG,"eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee");
            e.printStackTrace();
        }

        // Get the display size and density.
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
        mScreenDensity = metrics.densityDpi;

        mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(SAMPLE_RATE, 96 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(16, 400 * 1024, 90);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);

        StreamingProfile.Stream stream = new StreamingProfile.Stream(jsonObject);
        StreamingProfile streamingProfile = new StreamingProfile();
        streamingProfile
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setStream(stream)
                .setAVProfile(avProfile)
                .setPreferredVideoEncodingSize(mScreenWidth, mScreenHeight)
                .setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
        ;

        mVideoSourceConfig = new VideoSourceConfig();
        mVideoSourceConfig.setVideoSourceSize(240, 320);

        mStreamingManager = new StreamingManager(this, EncodingType.HW_SCREEN_VIDEO_WITH_HW_AUDIO_CODEC);
        mStreamingManager.prepare(streamingProfile);
        mStreamingManager.setStreamingStateListener(this);
        mStreamingManager.resume();
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (REQUEST_CODE_CAPTURE_PERM == requestCode) {
            if (resultCode == RESULT_OK && Build.VERSION.SDK_INT >= 21) {

                DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
                Display defaultDisplay = dm.getDisplay(Display.DEFAULT_DISPLAY);
                if (defaultDisplay == null) {
                    Toast.makeText(this, "No display found.", Toast.LENGTH_LONG).show();
                    return;
                }

                mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, intent);

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (mStreamingManager.startStreaming(mVideoSourceConfig)) {
                            Surface inputSurface = mStreamingManager.getInputSurface();
                            if (inputSurface != null) {
                                mScreenRecorder = new ScreenRecorder(mMediaProjection, inputSurface,
                                        mScreenWidth, mScreenHeight, mScreenDensity, SAMPLE_RATE, ScreenRecorderActivity.this);
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
                Intent permissionIntent = mMediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(permissionIntent, REQUEST_CODE_CAPTURE_PERM);
                break;
        }
    }
}
