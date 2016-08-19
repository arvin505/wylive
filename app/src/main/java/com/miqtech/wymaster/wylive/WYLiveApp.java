package com.miqtech.wymaster.wylive;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.pili.pldroid.streaming.StreamingEnv;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * Created by xiaoyi on 2016/8/15.
 */
public class WYLiveApp extends Application {
    private static Context mContext;
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
    }

    private void initApp() {
        mContext = this;
        refWatcher = LeakCanary.install(this);
        initImageLoader(this);
        StreamingEnv.init(this);
    }

    public static WYLiveApp getContext() {
        return (WYLiveApp) mContext;
    }

    public static RefWatcher getRefWatcher() {
        return getContext().refWatcher;
    }

    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(4 * 1024 * 1024).threadPoolSize(3)
                .diskCacheExtraOptions(480, 320, null)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
