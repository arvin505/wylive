package com.miqtech.wymaster.wylive.utils.imageloader;

import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.constants.API;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by xiaoyi on 2016/8/16.
 */
public class AsyncImage {

    private static final int DEFAULT_IMAGEID = R.drawable.icon_back;
    private static final String FILE = "file:///";
    private static final String DRAWABLE = "drawable://";
    private static final String SUFFIX = "!middle";

    private AsyncImage() {
        throw new RuntimeException("AsyncImage cloud not have instance");
    }

    /**
     * 显示网络图片
     *
     * @param url       图片地址
     * @param imageView imageview
     */
    public static void displayImage(String url, ImageView imageView) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.NORMAL, DEFAULT_IMAGEID, 0);
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    /**
     * 显示网络图片
     *
     * @param url       图片地址
     * @param imageView imageview
     * @param defImgId  默认图片id
     */
    public static void displayImage(String url, ImageView imageView, @DrawableRes int defImgId) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.NORMAL, defImgId, 0);
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    /**
     * 显示圆角图片
     *
     * @param url       图片地址
     * @param imageView imageview
     * @param radio     圆角半径
     */
    public static void displayRoundImage(String url, ImageView imageView, int radio) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.ROUND, DEFAULT_IMAGEID, radio);
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    /**
     * 显示圆角图片
     *
     * @param url       图片地址
     * @param imageView imageview
     * @param defaultId 默认图片地址
     * @param radio     圆角半径
     */
    public static void displayRoundImage(String url, ImageView imageView, @DrawableRes int defaultId, int radio) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.ROUND, defaultId, radio);
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    /**
     * 显示图片并回调
     *
     * @param url
     * @param imageView
     * @param listener
     */
    public static void displayImageWithCallback(String url, ImageView imageView, ImageLoadingListener listener) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.NORMAL, DEFAULT_IMAGEID, 0);
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    /**
     * 显示图片并回调
     *
     * @param url
     * @param imageView
     * @param defaultId
     * @param listener
     */
    public static void displayImageWithCallback(String url, ImageView imageView, @DrawableRes int defaultId, ImageLoadingListener listener) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.NORMAL, defaultId, 0);
        ImageLoader.getInstance().displayImage(url, imageView, options, listener);
    }

    /**
     * 不带缓存
     *
     * @param url
     * @param imageView
     */
    public static void displayImageNoCache(String url, ImageView imageView) {
        url = filterUrl(url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.NOCACHE, DEFAULT_IMAGEID, 0);
        ImageLoader.getInstance().displayImage(url, imageView, options);
    }

    private static String filterUrl(String url) {
        if (url == null || TextUtils.isEmpty(url)) return url;
        if (url.startsWith("http://") || url.startsWith("https://")) return url;
        return API.IMAGE_HOST + url + SUFFIX;
    }

}
