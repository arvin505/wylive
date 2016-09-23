package com.miqtech.wymaster.wylive.module.game.adapter;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.print.PageRange;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/25.
 */
public class GameMainAdapter extends BaseRecycleViewAdapter {
    private List<SearchWrapper<Object>> mDatas;
    private Context mContext;
    private LayoutInflater mInflater;

    private final int TYPE_TAG = 0;
    private final int TYPE_LIVE = 1;
    private final int TYPE_VIDEO = 2;

    public GameMainAdapter(List<SearchWrapper<Object>> mDatas, Context context) {
        this.mDatas = mDatas;
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case TYPE_TAG:
                convertView = mInflater.inflate(R.layout.layout_search_tag, parent, false);
                holder = new TagHolder(convertView);
                break;
            case TYPE_LIVE:
                convertView = mInflater.inflate(R.layout.layout_liveplay_item, parent, false);
                holder = new LiveHolder(convertView);
                break;
            case TYPE_VIDEO:
                convertView = mInflater.inflate(R.layout.layout_video_item, parent, false);
                holder = new VideoHolder(convertView);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TagHolder) {
            initTagView((TagHolder) holder, position);
        } else if (holder instanceof LiveHolder) {
            initLiveView((LiveHolder) holder, position);
        } else if (holder instanceof VideoHolder) {
            initVideoView((VideoHolder) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).type;
    }

    static class TagHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_tag_name)
        TextView tvTagName;
        @BindView(R.id.tv_tag_more)
        TextView tvTagMore;

        public TagHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class LiveHolder extends RecyclerView.ViewHolder {
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

        public LiveHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    static class VideoHolder extends RecyclerView.ViewHolder {
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

        public VideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void initTagView(TagHolder holder, final int position) {

        holder.tvTagMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.tvTagName.setText(mDatas.get(position).data.toString());
        int paddding = DeviceUtils.dp2px(mContext, 8);
        holder.itemView.setPadding(0, paddding, 0, paddding);
        holder.tvTagName.setCompoundDrawablePadding(DeviceUtils.dp2px(mContext,5));
        Drawable drawable = null;
        if (mDatas.get(position).data.toString().contains("直播")){
            drawable =  mContext.getResources().getDrawable(R.drawable.live_play_more);
        }else {
            drawable =  mContext.getResources().getDrawable(R.drawable.video_more);
        }
        Rect bounds = new Rect(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        drawable.setBounds(bounds);
        holder.tvTagName.setCompoundDrawables(drawable, null, null, null);
    }

    /**
     * 视频
     */
    private void initVideoView(VideoHolder holder, final int position) {
        ((ViewGroup) holder.itemView).setPadding(0, 0, 0, 0);
        LiveInfo video = (LiveInfo) mDatas.get(position).data;
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

    private void initLiveView(LiveHolder holder, final int position) {
        holder.bottomView.setVisibility(View.GONE);
        int padding = DeviceUtils.dp2px(mContext, 6);
        if (position % 2 == 0) {
            holder.itemView.setPadding(0, padding, padding, padding);
        } else {
            holder.itemView.setPadding(padding, padding, 0, padding);
        }
        LiveInfo info = (LiveInfo) mDatas.get(position).data;
        holder.tvGameName.setText(info.getGameName());
        holder.tvContent.setText(info.getTitle());
        holder.tvNickName.setText(info.getNickname());
        holder.anchorSex.setImageResource(info.getSex() == 0 ? R.drawable.live_play_men : R.drawable.live_play_femen);
        AsyncImage.displayImage(info.getUserIcon(), holder.anchorHeader, R.drawable.default_img);
        AsyncImage.displayImage(info.getIcon(), holder.ivLivePlayPoster, R.drawable.default_img);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }
}
