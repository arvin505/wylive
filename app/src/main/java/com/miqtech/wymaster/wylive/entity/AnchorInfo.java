package com.miqtech.wymaster.wylive.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by xiaoyi on 2016/8/22.
 */
public class AnchorInfo {

    @SerializedName("nickname")
    private String name;
    @SerializedName("room_id")
    private String room;
    private String id;
    private int state;
    private int fans;
    private String icon;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getFans() {
        return fans;
    }

    public void setFans(int fans) {
        this.fans = fans;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
