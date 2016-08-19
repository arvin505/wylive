package com.miqtech.wymaster.wylive.proxy;

import com.miqtech.wymaster.wylive.WYLiveApp;
import com.miqtech.wymaster.wylive.entity.User;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by xiaoyi on 2016/8/15.
 * 可添加userchangelistener
 * 在user变更的地方@see{setUser(User user)}
 * 通知改变
 * 应该用一个集合保存UserChangeListerner实例
 * 加or不加？？？？
 */
public class UserProxy {
    private static volatile User mUser;
    private final static String CACHE_DIR = WYLiveApp.getContext().getCacheDir().getAbsolutePath();

    public static User getUser() {
        mUser = readUser();
        return mUser;
    }

    public static void setUser(User user) {
        mUser = user;
        writeUser(user);
    }

    private static void writeUser(User user) {

        try {
            FileOutputStream fos = new FileOutputStream(CACHE_DIR + "/USER_CACHE");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static User readUser() {
        try {
            FileInputStream fis = new FileInputStream(CACHE_DIR + "/USER_CACHE");
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (User) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
