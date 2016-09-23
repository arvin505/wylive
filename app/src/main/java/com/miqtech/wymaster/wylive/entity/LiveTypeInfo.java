package com.miqtech.wymaster.wylive.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaoyi on 2016/8/4.
 */
public class LiveTypeInfo {
    private String icon; //图片

    @SerializedName(value = "name", alternate = {"game_name"})
    private String name;

    @SerializedName(value = "comment_num")
    private int commentNum;


    @SerializedName(value = "background_icon")
    private String poster;

    @SerializedName(value = "has_favor")
    private int hasFavor;

    @SerializedName("video_num")
    private int videoNum; //视频数

    private String id; //游戏类型id

    @SerializedName("live_num") //直播数
    private int liveNum;

    public String getIcon() {
        return icon;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getHasFavor() {
        return hasFavor;
    }

    public void setHasFavor(int hasFavor) {
        this.hasFavor = hasFavor;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVideoNum() {
        return videoNum;
    }

    public void setVideoNum(int videoNum) {
        this.videoNum = videoNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getLiveNum() {
        return liveNum;
    }

    public void setLiveNum(int liveNum) {
        this.liveNum = liveNum;
    }
}
