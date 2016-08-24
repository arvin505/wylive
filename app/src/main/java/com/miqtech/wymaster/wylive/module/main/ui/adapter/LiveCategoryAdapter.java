package com.miqtech.wymaster.wylive.module.main.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.LiveCategory;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/20.
 */
public class LiveCategoryAdapter extends BaseRecycleViewAdapter {

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
                ((TextView) convertView).setTextColor(mContext.getResources().getColor(R.color.category_item_count));
                ((TextView) convertView).setTextSize(15);
                convertView.setPadding(DeviceUtils.dp2px(mContext, 5), DeviceUtils.dp2px(mContext, 15), DeviceUtils.dp2px(mContext, 5), DeviceUtils.dp2px(mContext, 15));
                // convertView.setPadding(0, DeviceUtils.dp2px(mContext, 10), 0, DeviceUtils.dp2px(mContext, 10));
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
        if (mData.getHistory() == null || mData.getHistory().isEmpty()) {
            if (position > 0) {
                initItemView((LiveItemHolder) holder, position - 1);
            }
        } else {
            if (position == 0) {
                initRecentItemView((RecentHolder) holder);
            }
            if (position > 1) {
                initItemView((LiveItemHolder) holder, position - 2);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        if (mData.getHistory() == null || mData.getHistory() == null || mData.getHistory().isEmpty()) {
            if (mData.getGameList() == null || mData.getGameList().isEmpty()) {
                return 0;
            } else {
                return mData.getGameList().size() + 1;
            }
        } else {
            if (mData.getGameList() == null || mData.getGameList().size() == 0 || mData.getGameList().isEmpty()) {
                return 1;
            } else {
                return mData.getGameList().size() + 2;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.getHistory() == null || mData.getHistory().isEmpty()) {
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
        List<LiveTypeInfo> recents = mData.getHistory();
        for (int i = 0; i < 3; i++) {
            View itemView = mInflater.inflate(R.layout.layout_livecategory_item, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            itemView.setLayoutParams(params);
            llContent.addView(itemView);
            if (i >= recents.size()) {
                itemView.setVisibility(View.INVISIBLE);
                itemView.setClickable(false);
            } else {
                itemView.setVisibility(View.VISIBLE);
                itemView.setClickable(true);
            }
        }
        return convertView;
    }

    private void initRecentItemView(RecentHolder holder) {
        ViewGroup parent = (ViewGroup) holder.itemView.findViewById(R.id.llRecentContent);
        for (int i = 0; i < mData.getHistory().size() && i < 3; i++) {
            View view = parent.getChildAt(i);
            LiveTypeInfo data = mData.getHistory().get(i);
            TextView tvGameName = (TextView) view.findViewById(R.id.tv_game_name);
            TextView tvLiveCount = (TextView) view.findViewById(R.id.tv_live_count);
            TextView tvVideoCount = (TextView) view.findViewById(R.id.tv_video_count);
            RoundedImageView imgGameIcon = (RoundedImageView) view.findViewById(R.id.img_game_icon);
            tvGameName.setText(data.getName());
            tvLiveCount.setText(data.getLiveNum() + "直播");
            tvVideoCount.setText(data.getVideoNum() + "视频");
            AsyncImage.displayImage(data.getIcon(), imgGameIcon);

            final int finalI = i;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecentListener != null) {
                        mRecentListener.onRecentItemClick(v, finalI);
                    }
                }
            });
        }
    }

    private void initItemView(final LiveItemHolder holder, final int pos) {
        LiveTypeInfo data = mData.getGameList().get(pos);
        holder.tvGameName.setText(data.getName());
        holder.tvLiveCount.setText(data.getLiveNum() + "直播");
        holder.tvVideoCount.setText(data.getVideoNum() + "视频");
        AsyncImage.displayImage(data.getIcon(), holder.imgGameIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, pos);
            }
        });
    }


    private static class TagHolder extends RecyclerView.ViewHolder {

        public TagHolder(View itemView) {
            super(itemView);
        }
    }

    static class LiveItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_game_icon)
        RoundedImageView imgGameIcon;
        @BindView(R.id.tv_game_name)
        TextView tvGameName;
        @BindView(R.id.tv_live_count)
        TextView tvLiveCount;
        @BindView(R.id.tv_video_count)
        TextView tvVideoCount;

        public LiveItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class RecentHolder extends RecyclerView.ViewHolder {

        public RecentHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnRecentItemClickListener {
        void onRecentItemClick(View view, int pos);
    }

    private OnRecentItemClickListener mRecentListener;

    public void setOnRecentItemClickListener(OnRecentItemClickListener listener) {
        this.mRecentListener = listener;
    }
}
