package com.miqtech.wymaster.wylive.entity;

/**
 * Created by xiaoyi on 2016/8/23.
 */
public class SearchWrapper<T extends Object> {
    public int type;
    public Object data;
    public SearchWrapper(int type, Object data) {
        this.type = type;
        this.data = data;
    }
}
