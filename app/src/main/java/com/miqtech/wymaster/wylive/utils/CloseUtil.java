package com.miqtech.wymaster.wylive.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by arvin on 2016/8/19.
 */
public class CloseUtil {
    public static void close(Closeable c) {
        try {
            if (c != null)
                c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
