package com.miqtech.wymaster.wylive.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.WYLiveApp;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by zhaosentao on 2016/8/24.
 */
public class Utils {
    private static long lastClickTime = 0;

    /**
     * 禁止按钮的快速点击
     *
     * @return
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    /**
     * 检查网络状态  0 没有网络  1有wifi 2 有gprs 3 既有wifi也有gprs
     * @return
     */
    public static int checkNetworkState() {
        int type = 0;
        boolean flag=false;
        //得到网络连接信息
        ConnectivityManager manager = (ConnectivityManager) WYLiveApp.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if (!flag) {
            type=0;
        } else {
            NetworkInfo.State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if((gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING)
                    && (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)){
                type = 3;
            }else {
                if (gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING) {
                    type = 2;
                }
                //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
                if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
                    type = 1;
                }
            }

        }
        return type;
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * @param num  需要计算的数字
     * @param unit 单位 ： 1000   10000
     * @return
     */
    public static String calculate(int num, int unit, String unitStr) {
        if (num < unit) {
            return num + "";
        }
        float result = (float) num / (float) unit;
        DecimalFormat df = new DecimalFormat("#.0");
        return df.format(result) + unitStr;
    }

    /**
     * 得到数的形式 ： 6555 , 9.6万,9.6亿
     *
     * @param i
     * @return
     */
    public static String getnumberForms(int i, Context context) {
        String str = "";
        if (i < 10000) {
            str = i + "";
        } else if (i < 100000000) {
            str = i % 1000 / 10 + context.getResources().getString(R.string.wan);
        } else {
            str = i % 10000000 / 10 + context.getResources().getString(R.string.yi);
        }
        return str;
    }

    /**
     * 把18888888888变成188****8888
     *
     * @param str
     * @return
     */
    public static String changeString(String str) {
        if (str == null) {
            return null;
        }
        String strMobile = str;
        if (strMobile.length() < 11) {
            return str;
        }
        return strMobile.replace(str.substring(3, 7), "****").trim();
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 所有View的shape背景都可以用shape_orange_lien_bg.xml为基本模板
     * 设置shape   只能用于background引用于Shape的情况   不能用于Selector和layer-list
     *    strokeWidth默认为0 strokeColor默认透明   color默认为透明
     */
    public static void setShapeBackground(@NonNull View bgView, int strokeWidth, int strokeColor, int color, float radius) {

        GradientDrawable mGroupDrawable = (GradientDrawable) bgView.getBackground();
        //设置边框颜色和宽度
        if (strokeColor != 0 && strokeWidth != 0) {
            mGroupDrawable.setStroke(strokeWidth, WYLiveApp.getContext().getResources().getColor(strokeColor));
        } else {
            mGroupDrawable.setStroke(0, WYLiveApp.getContext().getResources().getColor(R.color.transparent));
        }
        //设置整体背景颜色
        mGroupDrawable.setColor(WYLiveApp.getContext().getResources().getColor(color == 0 ? R.color.transparent : color));

        //设置圆角
        mGroupDrawable.setCornerRadius(radius);
    }

    /**
     *   设置string 不同的字体颜色
     * @param content  文字内容
     * @param start    开始index
     * @param end      结束index
     * @param tv       View
     * @param color   不同字体的颜色
     */
    public static  void setFontDiffrentColor(String content, int start, int end, TextView tv, @ColorRes int color) {
        if (tv == null) {
            return;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(content);
        style.setSpan(new ForegroundColorSpan(WYLiveApp.getContext().getResources().getColor(color)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(style);
    }



}
