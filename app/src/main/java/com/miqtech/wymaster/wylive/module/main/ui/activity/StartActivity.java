package com.miqtech.wymaster.wylive.module.main.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by admin on 2016/9/14.
 */
@LayoutId(R.layout.activity_start)
public class StartActivity extends BaseAppCompatActivity {
    private Timer timer;
    private int countDown=2;
    private MyHandler myHandler;
    @Override
    protected void initViews(Bundle savedInstanceState) {
        myHandler = new MyHandler();
        timeOut();
    }
    private void timeOut() {
        timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            countDown--;
            Message message = new Message();
            myHandler.sendEmptyMessage(0);
        }
    };
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countDown == 0) {
                 timer.cancel();
                 jumpToActivity(MainActivity.class);
                finish();
            }
        }
    }
}
