package com.miqtech.wymaster.wylive.utils.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.text.TextUtils;
import android.widget.ImageView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.utils.L;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by xiaoyi on 2016/8/16.
 */
public class AsyncImage {

    private static final int DEFAULT_IMAGEID = R.drawable.default_img;
    private static final String FILE = "file:///";
    private static final String DRAWABLE = "drawable://";
    private static final String SUFFIX = "!small";

    private AsyncImage() {
        throw new RuntimeException("AsyncImage cloud not have instance");
    }

    static DisplayImageOptions yzmOption = new DisplayImageOptions.Builder()
            .showImageForEmptyUri(R.drawable.default_img).showImageOnFail(R.drawable.default_img)
            .showImageOnLoading(R.drawable.default_img).cacheInMemory(false).cacheOnDisk(false)
            .imageScaleType(ImageScaleType.EXACTLY).displayer(new FadeInBitmapDisplayer(200))
            .bitmapConfig(Bitmap.Config.RGB_565).build();

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

    public static void loadYZM(String url, ImageView view) {
        ImageLoader.getInstance().displayImage(url, view, yzmOption);
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
     * 显示网络图片
     *
     * @param url       图片地址
     * @param imageView imageview
     * @param defImgId  默认图片id
     */
    public static void displayImageSmall(String url, ImageView imageView, @DrawableRes int defImgId) {
        url = filterUrl(url) + SUFFIX;
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
        L.e("AsyncImage", "url" + url);
        DisplayImageOptions options = DisplayImageOptionsFactory.
                createOptions(DisplayImageOptionsFactory.Options.NORMAL, DEFAULT_IMAGEID, 0);
        ImageLoader.getInstance().displayImage(url, imageView, options, listener);
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
        return API.IMAGE_HOST + url ;
    }

}
