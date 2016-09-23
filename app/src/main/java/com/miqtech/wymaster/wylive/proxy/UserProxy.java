package com.miqtech.wymaster.wylive.proxy;


import com.miqtech.wymaster.wylive.WYLiveApp;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.utils.CloseUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

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
        if (mUser == null)
            mUser = readUser();
        if (mUser != null) {
            UserEventDispatcher.setUserState(new UserLoginState());
        } else {
            UserEventDispatcher.setUserState(new UserLogoutState());
        }
        return mUser;
    }

    public static void setUser(User user) {
        mUser = user;
        if (mUser == null) {
            UserEventDispatcher.setUserState(new UserLogoutState());
        }
        writeUser(mUser);
        notifyUserChanged(mUser);
    }

    private static void writeUser(User user) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(CACHE_DIR + "/USER_CACHE");
            oos = new ObjectOutputStream(fos);
            oos.writeObject(user);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(oos);
            CloseUtil.close(fos);
        }
    }

    private static User readUser() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(CACHE_DIR + "/USER_CACHE");
            ois = new ObjectInputStream(fis);
            return (User) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(ois);
            CloseUtil.close(fis);
        }
        return null;
    }

    /**
     * 监听user改变
     */
    public interface OnUserChangeListener {
        void onUserChange(User user);
    }

    private static List<OnUserChangeListener> mUserChangeListeners;

    /**
     * 添加userchangelistener
     *
     * @param listener
     */
    public static void addListener(OnUserChangeListener listener) {
        if (mUserChangeListeners == null) {
            synchronized (UserProxy.class) {
                if (mUserChangeListeners == null) {
                    mUserChangeListeners = new ArrayList<>();
                }
            }
        }
        mUserChangeListeners.add(listener);
    }

    public static void removeListener(OnUserChangeListener listener) {
        if (mUserChangeListeners == null) return;
        if (mUserChangeListeners.contains(listener)) {
            mUserChangeListeners.remove(listener);
            listener = null;
        }
    }

    private static void notifyUserChanged(User user) {
        if (mUserChangeListeners == null) return;
        for (OnUserChangeListener listener : mUserChangeListeners) {
            listener.onUserChange(user);
        }
    }

    public static void removeAllListener() {
        if (mUserChangeListeners == null) return;
        mUserChangeListeners.clear();
        mUserChangeListeners = null;
    }
}
