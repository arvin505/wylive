package com.miqtech.wymaster.wylive.utils.imageloader;

import android.database.CharArrayBuffer;
import android.graphics.Bitmap;
import android.print.PageRange;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * Created by xiaoyi on 2016/8/16.
 */
public class DisplayImageOptionsFactory {
    enum Options {
        NOCACHE, ROUND, NORMAL
    }

    private DisplayImageOptionsFactory() {
    }

    public static DisplayImageOptions createOptions(Options options, int defId, int radio) {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.bitmapConfig(Bitmap.Config.RGB_565)
                .showImageForEmptyUri(defId)
                .showImageOnFail(defId)
                .showImageOnLoading(defId)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY);
        generateOptions(builder, options, radio);
        return builder.build();
    }

    private static void generateOptions(DisplayImageOptions.Builder builder, Options options, int radio) {
        switch (options) {
            case NOCACHE:
                builder.cacheInMemory(false)
                        .cacheOnDisk(false);
                break;
            case ROUND:
                builder.displayer(new RoundedBitmapDisplayer(radio));
                break;
            default:
                break;
        }
    }
}
