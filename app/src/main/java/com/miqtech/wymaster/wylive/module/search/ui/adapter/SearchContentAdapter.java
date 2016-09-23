package com.miqtech.wymaster.wylive.module.search.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.entity.LiveTypeInfo;
import com.miqtech.wymaster.wylive.entity.SearchWrapper;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.utils.imageloader.ImageUtils;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
                convertView = mInflater.inflate(R.layout.layout_search_game, parent, false);
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
            initLiveView((LiveHolder) holder, position);
        } else if (holder instanceof AnchorHolder) {
            initAnchorView((AnchorHolder) holder, position);
        } else if (holder instanceof VideoHolder) {
            initVideoView((VideoHolder) holder, position);
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
            if (mDatas.get(position).data.toString().contains("相关游戏")) {
                holder.tvTagMore.setVisibility(View.GONE);
                holder.tvTagName.setTextColor(mContext.getResources().getColor(R.color.hall_title_font_white));
            } else {
                if (showMore(mDatas.get(position).data.toString())) {
                    holder.tvTagMore.setVisibility(View.VISIBLE);
                } else {
                    holder.tvTagMore.setVisibility(View.GONE);
                }
                holder.tvTagName.setTextColor(mContext.getResources().getColor(R.color.hall_title_more_font_gray));
            }
            holder.itemView.setPadding(0, DeviceUtils.dp2px(mContext, 10), 0, DeviceUtils.dp2px(mContext, 5));
        } else {
            holder.tvTagName.setTextColor(mContext.getResources().getColor(R.color.hall_title_more_font_gray));
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
        holder.tvTagName.setText(initTag(mDatas.get(position).data.toString()));
        if (showMore(mDatas.get(position).data.toString())) {
            holder.tvTagMore.setVisibility(View.VISIBLE);
        } else {
            holder.tvTagMore.setVisibility(View.GONE);
        }
    }

    private boolean showMore(String str) {
        if (str.contains("相关游戏")) return false;
        int count = Integer.parseInt(str.substring(4, str.length()));
        if (count > 4) {
            return true;
        }
        return false;
    }

    private String initTag(String tag){
        return tag.substring(0,4);
    }


    /**
     * 游戏
     */
    private void initGameView(GameHolder holder, final int position) {
        LiveTypeInfo game = (LiveTypeInfo) mDatas.get(position).data;
        holder.tvGameName.setText(game.getName());
        holder.tvLiveCount.setText(game.getLiveNum() + "");
        holder.tvVideoCount.setText(game.getVideoNum() + "");
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
            holder.tvAnchorState.setVisibility(View.VISIBLE);
            holder.tvAnchorState.setBackgroundResource(R.drawable.bg_rectangle_corner_solid_03);
            holder.tvAnchorRoom.setTextColor(mContext.getResources().getColor(R.color.attention_item_count));
            holder.tvAnchorFans.setTextColor(mContext.getResources().getColor(R.color.attention_item_count));
        } else {
            holder.tvAnchorState.setText("已离线");
            holder.tvAnchorState.setVisibility(View.GONE);
            holder.tvAnchorState.setBackgroundResource(R.drawable.bg_rectangle_corner_solid_04);
            holder.tvAnchorRoom.setTextColor(mContext.getResources().getColor(R.color.attention_item_flag));
            holder.tvAnchorFans.setTextColor(mContext.getResources().getColor(R.color.attention_item_flag));
        }
        AsyncImage.displayImageSmall(anchorInfo.getIcon(), holder.imgAnchorHeader, R.drawable.default_head);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(v, position);
            }
        });
    }

    /**
     * 视频
     */
    private void initVideoView(VideoHolder holder, final int position) {
        holder.lastLine.setVisibility(View.GONE);
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

    private void initLiveView(final LiveHolder holder, final int position) {
        holder.bottomView.setVisibility(View.GONE);
        int padding = DeviceUtils.dp2px(mContext, 6);
        if (position % 2 == 0) {
            holder.itemView.setPadding(padding, 0, 0, 0);
        } else {
            holder.itemView.setPadding(0, 0, padding, 0);
        }
        LiveInfo info = (LiveInfo) mDatas.get(position).data;
        holder.tvGameName.setText(info.getGameName());
        holder.tvContent.setText(info.getTitle());
        holder.tvNickName.setText(info.getNickname());
        holder.anchorSex.setImageResource(info.getSex() == 1 ? R.drawable.live_play_femen : R.drawable.live_play_men);
        AsyncImage.displayImageSmall(info.getUserIcon(), holder.anchorHeader, R.drawable.default_img);
        if (info.getScreen() == 0 && info.getSource() == 0&&info.getIcon().startsWith("http://pili")) {
            AsyncImage.displayImageWithCallback(info.getIcon(), holder.ivLivePlayPoster,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            holder.ivLivePlayPoster.setImageBitmap(ImageUtils.adjustPhotoRotation(
                                    ImageUtils.adjustPhotoRotation(ImageUtils.adjustPhotoRotation(
                                            loadedImage, 90),90),90));
                            //holder.ivLivePlayPoster.setImageBitmap((BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher)));
                        }
                    });
        } else {
            AsyncImage.displayImage(info.getIcon(), holder.ivLivePlayPoster, R.drawable.default_img);
        }
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
        ImageView imgGameIcon;
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

}
