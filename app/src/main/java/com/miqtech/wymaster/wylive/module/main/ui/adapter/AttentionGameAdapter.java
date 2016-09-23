package com.miqtech.wymaster.wylive.module.main.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/22.
 */
public class AttentionGameAdapter extends BaseRecycleViewAdapter {

    private Context mContext;
    private List<LiveTypeInfo> mDatas;
    private LayoutInflater mInflater;

    private static final int TYPE_ITEM = 0;

    public AttentionGameAdapter(Context context, List<LiveTypeInfo> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View convertView = null;
        switch (viewType) {
            case TYPE_ITEM:
                convertView = mInflater.inflate(R.layout.layout_livecategory_item, parent, false);
                holder = new ItemHolder(convertView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemHolder) {
            initItemView((ItemHolder) holder, position);
        }
    }


    private void initItemView(ItemHolder holder, final int position) {
        LiveTypeInfo data = mDatas.get(position);
        holder.tvGameName.setText(data.getName());
        holder.tvLiveCount.setText(data.getLiveNum() + "直播");
        holder.tvVideoCount.setText(data.getVideoNum() + "视频");
        AsyncImage.displayImage(data.getIcon(), holder.imgGameIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) return 0;
        return mDatas.size();
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_game_icon)
        ImageView imgGameIcon;
        @BindView(R.id.tv_game_name)
        TextView tvGameName;
        @BindView(R.id.tv_live_count)
        TextView tvLiveCount;
        @BindView(R.id.tv_video_count)
        TextView tvVideoCount;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }


}
