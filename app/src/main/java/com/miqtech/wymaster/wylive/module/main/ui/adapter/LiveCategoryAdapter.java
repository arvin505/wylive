package com.miqtech.wymaster.wylive.module.main.ui.adapter;

import android.content.Context;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.entity.LiveCategory;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;

import java.util.List;

/**
 * Created by xiaoyi on 2016/8/20.
 */
public class LiveCategoryAdapter extends RecyclerView.Adapter {
    private LiveCategory mData;

    private Context mContext;

    private LayoutInflater mInflater;

    private static final int VIEWTYPE_TAG = 0;
    private static final int VIEWTYPE_ITEM = 1;
    private static final int VIEWTYPE_RECENT = 2;

    public LiveCategoryAdapter(Context context, LiveCategory data) {
        this.mContext = context;
        this.mData = data;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View convertView = null;
        switch (viewType) {
            case VIEWTYPE_TAG:
                convertView = new TextView(mContext);
                ((TextView) convertView).setText("全部分类");
                convertView.setPadding(0, DeviceUtils.dp2px(mContext, 10), 0, DeviceUtils.dp2px(mContext, 10));
                holder = new TagHolder(convertView);
                break;
            case VIEWTYPE_ITEM:
                convertView = mInflater.inflate(R.layout.layout_livecategory_item, parent, false);
                holder = new LiveItemHolder(convertView);
                break;
            case VIEWTYPE_RECENT:
                convertView = generateRecent();
                holder = new RecentHolder(convertView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        // FIXME: 2016/8/20 具体逻辑
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        if (mData.getRecentPlay() == null || mData.getRecentPlay().isEmpty()) {
            if (mData.getAllPlay() == null || mData.getAllPlay().isEmpty()) {
                return 0;
            } else {
                return mData.getAllPlay().size() + 1;
            }
        } else {
            if (mData.getAllPlay().size() == 0 || mData.getAllPlay().isEmpty()) {
                return mData.getRecentPlay().size() + 1;
            } else {
                return mData.getAllPlay().size() + mData.getRecentPlay().size() + 2;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.getRecentPlay() == null || mData.getRecentPlay().isEmpty()) {
            if (position == 0) {
                return VIEWTYPE_TAG;
            }
            return VIEWTYPE_ITEM;
        } else {
            if (position == 0) {
                return VIEWTYPE_RECENT;
            } else if (position == 1) {
                return VIEWTYPE_TAG;
            } else {
                return VIEWTYPE_ITEM;
            }
        }
    }

    private View generateRecent() {
        View convertView = mInflater.inflate(R.layout.layout_livecategory_recent, null);
        LinearLayout llContent = (LinearLayout) convertView.findViewById(R.id.llRecentContent);
        List<LiveTypeInfo> recents = mData.getRecentPlay();
        for (int i = 0; i < recents.size() && i < 3; i++) {
            View itemView = mInflater.inflate(R.layout.layout_livecategory_item, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            itemView.setLayoutParams(params);
            llContent.addView(itemView);
        }
        return convertView;
    }

    private static class TagHolder extends RecyclerView.ViewHolder {

        public TagHolder(View itemView) {
            super(itemView);
        }
    }

    private static class LiveItemHolder extends RecyclerView.ViewHolder {

        public LiveItemHolder(View itemView) {
            super(itemView);
        }
    }

    private static class RecentHolder extends RecyclerView.ViewHolder {

        public RecentHolder(View itemView) {
            super(itemView);
        }
    }
}
