package com.miqtech.wymaster.wylive.module.search.ui.activity.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/23.
 */
public class SearchContentAdapter extends BaseRecycleViewAdapter {

    private static final int VIEWTYPE_TAG = 0;
    private static final int VIEWTYPE_GAME = 1;
    private static final int VIEWTYPE_LIVE = 2;
    private static final int VIEWTYPE_ANCHOR = 3;
    private static final int VIEWTYPE_VIDEO = 4;

    private Context mContext;
    private List<SearchWrapper<Object>> mDatas;
    private LayoutInflater mInflater;

    public SearchContentAdapter(Context mContext, List<SearchWrapper<Object>> mData) {
        this.mContext = mContext;
        this.mDatas = mData;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case VIEWTYPE_TAG:
                convertView = mInflater.inflate(R.layout.layout_search_tag, parent, false);
                holder = new TagHolder(convertView);
                break;
            case VIEWTYPE_GAME:
                convertView = mInflater.inflate(R.layout.layout_livecategory_item, parent, false);
                holder = new GameHolder(convertView);
                break;
            case VIEWTYPE_ANCHOR:
                convertView = mInflater.inflate(R.layout.layout_attention_anchor_item, parent, false);
                holder = new AnchorHolder(convertView);
                break;
            case VIEWTYPE_VIDEO:
                convertView = mInflater.inflate(R.layout.layout_video_item, parent, false);
                holder = new VideoHolder(convertView);
                break;
            case VIEWTYPE_LIVE:
                convertView = mInflater.inflate(R.layout.layout_liveplay_item, parent, false);
                holder = new LiveHolder(convertView);
                break;

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TagHolder) {
            initTagView((TagHolder) holder, position);
        } else if (holder instanceof GameHolder) {
            initGameView((GameHolder) holder, position);
        } else if (holder instanceof LiveHolder) {

        } else if (holder instanceof AnchorHolder) {
            initAnchorView((AnchorHolder) holder, position);
        } else if (holder instanceof VideoHolder) {

        }
    }

    /**
     * 搜索类型
     *
     * @param holder
     * @param position
     */
    private void initTagView(TagHolder holder, final int position) {
        if (position == 0) {
            if (mDatas.get(position).data.toString().equals("游戏")) {
                holder.tvTagMore.setVisibility(View.GONE);
            } else {
                holder.tvTagMore.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tvTagMore.setVisibility(View.VISIBLE);
        }
        holder.tvTagMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
        holder.tvTagName.setText(mDatas.get(position).data.toString());

    }

    /**
     * 游戏
     */
    private void initGameView(GameHolder holder, final int position) {
        LiveTypeInfo game = (LiveTypeInfo) mDatas.get(position).data;
        holder.tvGameName.setText(game.getName());
        holder.tvLiveCount.setText(game.getLiveNum() + "直播");
        holder.tvVideoCount.setText(game.getVideoNum() + "视频");
        AsyncImage.displayImage(game.getIcon(), holder.imgGameIcon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(v, position);
                }
            }
        });
    }

    /**
     * 主播
     */
    private void initAnchorView(AnchorHolder holder, final int position) {
        AnchorInfo anchorInfo = (AnchorInfo) mDatas.get(position).data;
        holder.tvAnchorFans.setText("" + anchorInfo.getFans());
        holder.tvAnchorRoom.setText("" + anchorInfo.getRoom());
        holder.tvAnchorName.setText("" + anchorInfo.getName());
        if (anchorInfo.getState() == 1) {
            holder.tvAnchorState.setText("直播中");
            holder.tvAnchorState.setBackgroundResource(R.drawable.bg_rectangle_corner_solid_03);
        } else {
            holder.tvAnchorState.setText("已离线");
            holder.tvAnchorState.setBackgroundResource(R.drawable.bg_rectangle_corner_solid_04);
        }
        AsyncImage.displayImage(anchorInfo.getIcon(), holder.imgAnchorHeader, R.drawable.default_head);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mDatas == null) return 0;
        return mDatas.size();
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

    static class GameHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_game_icon)
        RoundedImageView imgGameIcon;
        @BindView(R.id.tv_game_name)
        TextView tvGameName;
        @BindView(R.id.tv_live_count)
        TextView tvLiveCount;
        @BindView(R.id.tv_video_count)
        TextView tvVideoCount;

        public GameHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class LiveHolder extends RecyclerView.ViewHolder {

        public LiveHolder(View itemView) {
            super(itemView);
        }
    }

    static class AnchorHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_anchor_header)
        CircleImageView imgAnchorHeader;
        @BindView(R.id.tv_anchor_room)
        TextView tvAnchorRoom;
        @BindView(R.id.tv_anchor_fans)
        TextView tvAnchorFans;
        @BindView(R.id.tv_anchor_state)
        TextView tvAnchorState;
        @BindView(R.id.tv_anchor_name)
        TextView tvAnchorName;

        public AnchorHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class VideoHolder extends RecyclerView.ViewHolder {

        public VideoHolder(View itemView) {
            super(itemView);
        }
    }

}
