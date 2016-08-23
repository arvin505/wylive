package com.miqtech.wymaster.wylive.entity;

import java.util.List;

/**
 * Created by xiaoyi on 2016/8/23.
 */
public class SearchContent {

    private LiveTypeInfo game;
    private List<AnchorInfo> anchors;
    private List<LiveTypeInfo> lives;
    private List<AnchorInfo> videos;

    public LiveTypeInfo getGame() {
        return game;
    }

    public void setGame(LiveTypeInfo game) {
        this.game = game;
    }

    public List<AnchorInfo> getAnchors() {
        return anchors;
    }

    public void setAnchors(List<AnchorInfo> anchors) {
        this.anchors = anchors;
    }

    public List<LiveTypeInfo> getLives() {
        return lives;
    }

    public void setLives(List<LiveTypeInfo> lives) {
        this.lives = lives;
    }

    public List<AnchorInfo> getVideos() {
        return videos;
    }

    public void setVideos(List<AnchorInfo> videos) {
        this.videos = videos;
    }
}
