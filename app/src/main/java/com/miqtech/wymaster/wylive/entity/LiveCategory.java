package com.miqtech.wymaster.wylive.entity;

import java.util.List;

/**
 * Created by xiaoyi on 2016/8/20.
 */
public class LiveCategory {

    private List<LiveTypeInfo> recentPlay;
    private List<LiveTypeInfo> allPlay;

    public List<LiveTypeInfo> getRecentPlay() {
        return recentPlay;
    }

    public void setRecentPlay(List<LiveTypeInfo> recentPlay) {
        this.recentPlay = recentPlay;
    }

    public List<LiveTypeInfo> getAllPlay() {
        return allPlay;
    }

    public void setAllPlay(List<LiveTypeInfo> allPlay) {
        this.allPlay = allPlay;
    }
}
