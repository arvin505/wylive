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
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/28.
 */
public class LiveListAdapter extends BaseRecycleViewAdapter {
    private Context mContext;
    private List<LiveInfo> mDatas;
    private LayoutInflater mInflater;

    public LiveListAdapter(Context mContext, List<LiveInfo> mDatas) {
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
        initLiveView((ItemHolder) holder, position);
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) return 0;
        return mDatas.size();
    }

    /**
     * 视频
     */
    private void initLiveView(ItemHolder holder, final int position) {
        holder.bottomView.setVisibility(View.GONE);
        int padding = DeviceUtils.dp2px(mContext, 6);
        if (position % 2 == 0) {
            holder.itemView.setPadding(padding, 0, 0, 0);
        } else {
            holder.itemView.setPadding(0, 0, padding, 0);
        }
        LiveInfo info = (LiveInfo) mDatas.get(position);
        holder.tvGameName.setText(info.getGameName());
        holder.tvContent.setText(info.getTitle());
        holder.tvNickName.setText(info.getNickname());
        holder.anchorSex.setImageResource(info.getSex() == 1 ? R.drawable.live_play_men : R.drawable.live_play_femen);
        AsyncImage.displayImage(info.getUserIcon(), holder.anchorHeader, R.drawable.default_img);
        AsyncImage.displayImage(info.getIcon(), holder.ivLivePlayPoster, R.drawable.default_img);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener!=null){
                    mItemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bottomView)
        View bottomView;
        @BindView(R.id.ivLivePlayPoster)
        RoundedImageView ivLivePlayPoster;
        @BindView(R.id.anchorHeader)
        CircleImageView anchorHeader;
        @BindView(R.id.anchorSex)
        ImageView anchorSex;
        @BindView(R.id.tvGameName)
        TextView tvGameName;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvNickName)
        TextView tvNickName;

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
