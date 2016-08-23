package com.miqtech.wymaster.wylive.utils;

import java.text.DecimalFormat;

/**
 * Created by admin on 2016/8/22.
 */
public class Utils {
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
}
