package com.miqtech.wymaster.wylive.entity;

import java.util.List;

/**
 * Created by xiaoyi on 2016/8/20.
 */
public class LiveCategory {

    private List<LiveTypeInfo> history;
    private List<LiveTypeInfo> gameList;

    public List<LiveTypeInfo> getHistory() {
        return history;
    }

    public void setHistory(List<LiveTypeInfo> history) {
        this.history = history;
    }

    public List<LiveTypeInfo> getGameList() {
        return gameList;
    }

    public void setGameList(List<LiveTypeInfo> gameList) {
        this.gameList = gameList;
    }
}
