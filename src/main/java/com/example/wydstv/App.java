package com.example.wydstv;

import android.app.Application;

import com.pili.pldroid.streaming.StreamingEnv;

/**
 * Created by xiaoyi on 2016/7/20.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        StreamingEnv.init(getApplicationContext());
    }
}
