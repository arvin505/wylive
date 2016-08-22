package com.miqtech.wymaster.wylive.common;

import android.print.PrintJob;
import android.support.v7.widget.RecyclerView;

/**
 * Created by xiaoyi on 2016/8/22.
 */
public abstract class BaseRecycleViewAdapter extends RecyclerView.Adapter {
    protected RecycleViewItemClickListener mItemClickListener;

    public void setOnItemClickListener(RecycleViewItemClickListener listener) {
        mItemClickListener = listener;
    }
}
