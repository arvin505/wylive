package com.miqtech.wymaster.wylive.module.search.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/28.
 */
public class VideoListAdapter extends BaseRecycleViewAdapter {
    private Context mContext;
    private List<LiveInfo> mDatas;
    private LayoutInflater mInflater;

    public VideoListAdapter(Context mContext, List<LiveInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View convertView = null;
        convertView = mInflater.inflate(R.layout.layout_video_item, parent, false);
        holder = new ItemHolder(convertView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        initVideoView((ItemHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) return 0;
        return mDatas.size();
    }

    /**
     * 视频
     */
    private void initVideoView(ItemHolder holder, final int position) {
        holder.lastLine.setVisibility(View.GONE);
        ((ViewGroup) holder.itemView).setPadding(0, 0, 0, 0);
        LiveInfo video = (LiveInfo) mDatas.get(position);
        holder.tvCommentNum.setText(video.getCommentNum() + "");
        holder.tvPlayNum.setText(video.getPlayTimes() + "");
        holder.tvVideoTitle.setText(video.getTitle());
        holder.tvVideoContent.setText(video.getNickname());
        AsyncImage.displayImage(video.getIcon(), holder.ivVideoPic);
        holder.imgSex.setImageResource(video.getSex() == 1 ? R.drawable.icon_male_video : R.drawable.icon_female_video);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.last_line)
        View lastLine;
        @BindView(R.id.ivVideoPic)
        RoundedImageView ivVideoPic;
        @BindView(R.id.tvVideoTitle)
        TextView tvVideoTitle;
        @BindView(R.id.tvVideoContent)
        TextView tvVideoContent;
        @BindView(R.id.tvPlayNum)
        TextView tvPlayNum;
        @BindView(R.id.tvCommentNum)
        TextView tvCommentNum;
        @BindView(R.id.img_sex)
        ImageView imgSex;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
