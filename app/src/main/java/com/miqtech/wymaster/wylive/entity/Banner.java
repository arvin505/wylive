package com.miqtech.wymaster.wylive.entity;

public class Banner {
    private String img; //图片
    int id;
    private int type; //类型：1直播 2视频 3url
    private String target; //目标对象的ID或者url地址
    private String title; //标题

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
