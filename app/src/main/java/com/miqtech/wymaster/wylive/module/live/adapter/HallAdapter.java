package com.miqtech.wymaster.wylive.module.live.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.entity.Banner;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.module.live.LivePlayListActivity;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.live.LiveVideoListActivity;
import com.miqtech.wymaster.wylive.module.live.PlayVideoActivity;
import com.miqtech.wymaster.wylive.module.main.ui.activity.MainActivity;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.utils.imageloader.ImageUtils;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.miqtech.wymaster.wylive.widget.HeadLinesView;
import com.miqtech.wymaster.wylive.widget.roundImageView.RoundedImageView;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * Created by admin on 2016/8/22.
 */
public class HallAdapter extends RecyclerView.Adapter {
    private static final String TAG = "LivePlayAdapter";
    private List<LiveInfo> liveDatas; //直播列表数据
    private List<LiveInfo> videoDatas;//视频数据
    private Context mContext;
    private LayoutInflater inflater;


    public static final int VIEW_EMPTY = -1;
    public static final int VIEW_LIVE_TITLE = 1;
    public static final int VIEW_LIVE_ITEM = 2;
    public static final int VIEW_VIDEO_TITLE = 3;
    public static final int VIEW_VIDEO_ITEM = 4;
    public static final int VIEW_BANNER = 5;

    private int type = 0; //0 直播列表标题和视屏列表标题都显示 1 隐藏直播标题 2 隐藏视频标题
    private boolean isHasNetwork = true;
    private List<Banner> banners;
    private int banner = 0;

    public HallAdapter(Context context, int type, List<LiveInfo> liveDatas, List<LiveInfo> videoDatas) {
        this.type = type;
        this.liveDatas = liveDatas;
        this.videoDatas = videoDatas;
        this.mContext = context;
        this.inflater = LayoutInflater.from(context);
    }

    public void setBannerData(List<Banner> bannerDatas) {
        this.banners = bannerDatas;
        if (banners != null && !banners.isEmpty()) {
            banner = 1;
        } else {
            banner = 0;
        }
    }

    public void setNetWorkState(boolean isHasNetwork) {
        this.isHasNetwork = isHasNetwork;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        L.e(TAG, "onCreateViewHolder" + viewType);
        if (viewType == VIEW_BANNER) {
            view = inflater.inflate(R.layout.layout_banner, null);
            holder = new HallBanner(view);
        } else if (viewType == VIEW_LIVE_TITLE) {
            view = inflater.inflate(R.layout.layout_livelist_title_view, null);
            holder = new LivePlayTitle(view);
        } else if (viewType == VIEW_LIVE_ITEM) {
            view = inflater.inflate(R.layout.layout_liveplay_item, null);
            holder = new LivePlayItemHolder(view);
        } else if (viewType == VIEW_VIDEO_TITLE) {
            view = inflater.inflate(R.layout.layout_livelist_title_view, null);
            holder = new LivePlayTitle(view);
        } else if (viewType == VIEW_VIDEO_ITEM) {
            view = inflater.inflate(R.layout.layout_video_item, null);
            holder = new VideoItemHolder(view);
        } else {
            //TODO 处理异常页面
            view = inflater.inflate(R.layout.layout_exception_page, parent, false);
            holder = new EmptyHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HallBanner) {
            setBanner((HallBanner) holder);
        } else if (holder instanceof LivePlayTitle) {
            setTitle((LivePlayTitle) holder, position);
        } else if (holder instanceof LivePlayItemHolder) {
            setLivePlay((LivePlayItemHolder) holder, position);
        } else if (holder instanceof VideoItemHolder) {
            setupVideo((VideoItemHolder) holder, position);
        } else if (holder instanceof EmptyHolder) {
            if (isHasNetwork) {
                if (type == 0) {
                    ((EmptyHolder) holder).errTitle.setText("该栏目下没有视频和直播");
                } else if (type == 1) {
                    ((EmptyHolder) holder).errTitle.setText("直播被怪兽抓走了，我们正在全力营救!");
                } else if (type == 2) {
                    ((EmptyHolder) holder).errTitle.setText("该栏目下没有视频");
                }
            } else {
                ((EmptyHolder) holder).errTitle.setText("网络不给力,请检查网络再试试");
                isHasNetwork = true;
            }
        }
    }

    /**
     * 设置banner数据
     */
    private void setBanner(HallBanner holder) {

        holder.hlvHallBanner.refreshData(banners);
    }


    private void setTitle(LivePlayTitle holder, int position) {
        if ((liveDatas != null && !liveDatas.isEmpty()) && (videoDatas != null && videoDatas.isEmpty())) {
            if (position == banner) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_liveplay));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_play_more, 0, 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到直播列表
                        Intent intent = new Intent(mContext, LivePlayListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if ((liveDatas != null && liveDatas.isEmpty()) && (videoDatas != null && !videoDatas.isEmpty())) {
            if (position == banner) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_video));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_more, 0, 0, 0);
                holder.rlTitle.setPadding(0, DeviceUtils.dp2px(mContext, 9), 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到视频列表
                        Intent intent = new Intent(mContext, LiveVideoListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        } else if ((liveDatas != null && !liveDatas.isEmpty()) && (videoDatas != null && !videoDatas.isEmpty())) {
            if (position == banner) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_liveplay));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.live_play_more, 0, 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到直播列表
                        Intent intent = new Intent(mContext, LivePlayListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            } else if (position == liveDatas.size() + 1 + banner) {
                holder.tvHotLivePlay.setText(mContext.getResources().getString(R.string.hot_video));
                holder.tvHotLivePlay.setCompoundDrawablesWithIntrinsicBounds(R.drawable.video_more, 0, 0, 0);
                holder.tvMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 跳转到视频列表
                        Intent intent = new Intent(mContext, LiveVideoListActivity.class);
                        mContext.startActivity(intent);
                    }
                });
            }
        }
        //TODO 设置跳转事件
    }

    /**
     * 填充直播数据
     */
    private void setLivePlay(final LivePlayItemHolder holder, int position) {
        if (liveDatas == null || (liveDatas != null && liveDatas.isEmpty())) {
            return;
        }
        LiveInfo data = null;
        if (type == 1) {
            data = liveDatas.get(position);
        } else {
            data = liveDatas.get(position - 1 - banner);
        }
        if (data != null) {

            AsyncImage.displayImage(data.getUserIcon(), holder.anchorHeader, R.drawable.default_head);
            holder.tvNickName.setText(data.getNickname());
            holder.tvContent.setText(data.getTitle());
            if (data.getScreen() == 0 && data.getSource() == 0 && data.getIcon().startsWith("http://pili")) {
                AsyncImage.displayImageWithCallback(data.getIcon(), holder.ivLivePlayPoster,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                super.onLoadingComplete(imageUri, view, loadedImage);
                                holder.ivLivePlayPoster.setImageBitmap(ImageUtils.adjustPhotoRotation(
                                        ImageUtils.adjustPhotoRotation(ImageUtils.adjustPhotoRotation(
                                                loadedImage, 90), 90), 90));
                                //holder.ivLivePlayPoster.setImageBitmap((BitmapFactory.decodeResource(mContext.getResources(),R.mipmap.ic_launcher)));
                            }
                        });
            } else {
                AsyncImage.displayImage(data.getIcon(), holder.ivLivePlayPoster, R.drawable.default_img);
            }
            if (!TextUtils.isEmpty(data.getGameName())) {
                holder.tvGameName.setVisibility(View.VISIBLE);
                holder.tvGameName.setText(data.getGameName());
            } else {
                holder.tvGameName.setVisibility(View.GONE);
            }
            if (data.getSex() == 0) {
                holder.anchorSex.setImageResource(R.drawable.live_play_men);
            } else {
                holder.anchorSex.setImageResource(R.drawable.live_play_femen);
            }
            final LiveInfo finalData = data;
            holder.llLivePlayItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, LiveRoomActivity.class);
                    intent.putExtra("id", finalData.getId() + "");
                    intent.putExtra("rtmp", finalData.getRtmp());
                    mContext.startActivity(intent);
                }
            });
            if (type == 1) {
                if (position % 2 == 0) {
                    holder.llLivePlayItem.setPadding(DeviceUtils.dp2px(mContext, 12), 0, DeviceUtils.dp2px(mContext, 6), 0);
                } else {
                    holder.llLivePlayItem.setPadding(DeviceUtils.dp2px(mContext, 6), 0, DeviceUtils.dp2px(mContext, 12), 0);
                }

            } else {
                if ((position + banner) % 2 == 1) {
                    holder.llLivePlayItem.setPadding(DeviceUtils.dp2px(mContext, 12), 0, DeviceUtils.dp2px(mContext, 6), 0);
                } else {
                    holder.llLivePlayItem.setPadding(DeviceUtils.dp2px(mContext, 6), 0, DeviceUtils.dp2px(mContext, 12), 0);
                }
                //最底部的view影藏
                if (liveDatas.size() % 2 == 0) {
                    if (position == liveDatas.size() + banner || position == liveDatas.size() + banner - 1) {
                        holder.bottomView.setVisibility(View.GONE);
                    } else {
                        holder.bottomView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (position == liveDatas.size() + banner) {
                        holder.bottomView.setVisibility(View.GONE);
                    } else {
                        holder.bottomView.setVisibility(View.VISIBLE);
                    }
                }

            }
            holder.ivLivePlayPoster.measure(-1, -1);
            final int width = (DeviceUtils.deviceWidth(mContext) - DeviceUtils.dp2px(mContext, 12) * 3) / 2;
            ViewGroup.LayoutParams lp = holder.ivLivePlayPoster.getLayoutParams();
            lp.height = width * 9 / 16;
            lp.width = width;
            // 按一定的比例去显示图片高度
            holder.ivLivePlayPoster.setLayoutParams(lp);

            RelativeLayout.LayoutParams rlParams = (RelativeLayout.LayoutParams) holder.rlAnchorInfo.getLayoutParams();
            rlParams.setMargins(DeviceUtils.dp2px(mContext, 5), lp.height - DeviceUtils.dp2px(mContext, 21), 0, 0);
            RelativeLayout.LayoutParams tvContentParams = (RelativeLayout.LayoutParams) holder.tvContent.getLayoutParams();
            tvContentParams.setMargins(DeviceUtils.dp2px(mContext, 5), DeviceUtils.dp2px(mContext, 12), 0, DeviceUtils.dp2px(mContext, 12));
        }

    }

    /**
     * 填充视频数据
     */
    private void setupVideo(VideoItemHolder holder, final int position) {
        LiveInfo data = null;
        if (videoDatas == null || (videoDatas != null && videoDatas.isEmpty())) {
            return;
        }
        if (liveDatas != null && !liveDatas.isEmpty()) {
            if (type == 2) {
                data = videoDatas.get(position);
            } else {
                data = videoDatas.get(position - liveDatas.size() - 2 - banner);
            }
        } else {
            //如果直播数据为空的时候
            if (type == 2) {
                data = videoDatas.get(position);
            } else {
                data = videoDatas.get(position - 1 - banner);
            }
        }
        if (data != null) {
            L.e(TAG, "setupVideo !=null");
            AsyncImage.displayImage(data.getIcon(), holder.ivVideoPic);
            holder.tvPlayNum.setText(Utils.calculate(data.getPlayTimes(), 10000, "万"));
            holder.tvVideoTitle.setText(data.getTitle());
            holder.tvVideoContent.setText(data.getNickname());
            holder.tvCommentNum.setText(Utils.calculate(data.getCommentNum(), 10000, "万"));
            holder.img_sex.setImageResource(data.getSex() == 0 ? R.drawable.icon_male_video : R.drawable.icon_female_video);
        }

        final LiveInfo finalData = data;
        holder.llVideoItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayVideoActivity.class);
                intent.putExtra("videoId", finalData.getId() + "");
                intent.putExtra("rtmp", finalData.getRtmp());
                mContext.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        int liveNum = 0;
        int videoNum = 0;
        if ((liveDatas != null && liveDatas.isEmpty() && (videoDatas != null && videoDatas.isEmpty()))) {
            return 1;
        } else {
            if (liveDatas != null && !liveDatas.isEmpty()) {
                if (type == 1) {
                    liveNum = liveDatas.size();
                } else {
                    liveNum = liveDatas.size() + 1 + banner;
                }
            } else {
                liveNum = 0 + banner;
            }
            if (videoDatas != null && !videoDatas.isEmpty()) {
                if (type == 2) {
                    videoNum = videoDatas.size();
                } else {
                    videoNum = videoDatas.size() + 1;
                }
            } else {
                videoNum = 0;
            }
            return liveNum + videoNum;
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (banners != null && !banners.isEmpty()) {
            if (position == 0) {
                return VIEW_BANNER;
            } else {
                return setItemViewType(position, 1);
            }
        } else {
            if ((liveDatas != null && liveDatas.isEmpty() && (videoDatas != null && videoDatas.isEmpty()))) {
                return VIEW_EMPTY;
            } else {
                return setItemViewType(position, 0);
            }
        }
    }

    private int setItemViewType(int position, int isHaveBanner) {
        if ((liveDatas != null && !liveDatas.isEmpty()) && (videoDatas != null && videoDatas.isEmpty())) {
            if (position == 0 + isHaveBanner) {
                if (type == 1) {
                    return VIEW_LIVE_ITEM;
                } else {
                    return VIEW_LIVE_TITLE;
                }
            } else {
                return VIEW_LIVE_ITEM;
            }
        } else if ((liveDatas != null && liveDatas.isEmpty()) && (videoDatas != null && !videoDatas.isEmpty())) {
            if (position == 0 + isHaveBanner) {
                if (type == 2) {
                    return VIEW_VIDEO_ITEM;
                } else {
                    return VIEW_VIDEO_TITLE;
                }
            } else {
                return VIEW_VIDEO_ITEM;
            }
        } else {
            if (position == 0 + isHaveBanner) {
                return VIEW_LIVE_TITLE;
            } else if (position > (0 + isHaveBanner) && position <= liveDatas.size() + (0 + isHaveBanner)) {
                return VIEW_LIVE_ITEM;
            } else if (position == liveDatas.size() + (0 + isHaveBanner) + 1) {
                return VIEW_VIDEO_TITLE;
            } else {
                return VIEW_VIDEO_ITEM;
            }
        }
    }

    class LivePlayItemHolder extends RecyclerView.ViewHolder {
        LinearLayout llLivePlayItem;
        RoundedImageView ivLivePlayPoster;
        CircleImageView anchorHeader;
        TextView tvNickName;
        TextView tvContent;
        View bottomView;
        TextView tvGameName;
        ImageView anchorSex;
        RelativeLayout rlAnchorInfo;

        public LivePlayItemHolder(View itemView) {
            super(itemView);
            rlAnchorInfo = (RelativeLayout) itemView.findViewById(R.id.rlAnchorInfo);
            bottomView = itemView.findViewById(R.id.bottomView);//顶部view间距
            llLivePlayItem = (LinearLayout) itemView.findViewById(R.id.llLivePlayItem);
            ivLivePlayPoster = (RoundedImageView) itemView.findViewById(R.id.ivLivePlayPoster);
            anchorHeader = (CircleImageView) itemView.findViewById(R.id.anchorHeader);
            tvNickName = (TextView) itemView.findViewById(R.id.tvNickName);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            tvGameName = (TextView) itemView.findViewById(R.id.tvGameName);
            anchorSex = (ImageView) itemView.findViewById(R.id.anchorSex);

        }
    }

    class VideoItemHolder extends RecyclerView.ViewHolder {
        LinearLayout llVideoItem;
        RoundedImageView ivVideoPic;
        TextView tvVideoTitle;
        TextView tvVideoContent;
        TextView tvPlayNum;
        TextView tvCommentNum;
        ImageView img_sex;

        public VideoItemHolder(View itemView) {
            super(itemView);
            llVideoItem = (LinearLayout) itemView.findViewById(R.id.llVideoItem); //整个item
            ivVideoPic = (RoundedImageView) itemView.findViewById(R.id.ivVideoPic);  //视屏图片
            tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle); //视屏标题
            tvVideoContent = (TextView) itemView.findViewById(R.id.tvVideoContent);//视屏类容
            tvPlayNum = (TextView) itemView.findViewById(R.id.tvPlayNum); //播放次数
            tvCommentNum = (TextView) itemView.findViewById(R.id.tvCommentNum); //点赞次数
            img_sex = (ImageView) itemView.findViewById(R.id.img_sex); //主播性别
        }
    }

    class LivePlayTitle extends RecyclerView.ViewHolder {
        TextView tvHotLivePlay; //热门直播
        TextView tvMore;  //查看更多
        RelativeLayout rlTitle; //标题

        public LivePlayTitle(View itemView) {
            super(itemView);
            tvHotLivePlay = (TextView) itemView.findViewById(R.id.tvHotLivePlay);
            tvMore = (TextView) itemView.findViewById(R.id.tvMore);
            rlTitle = (RelativeLayout) itemView.findViewById(R.id.rlTitle);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    class EmptyHolder extends RecyclerView.ViewHolder {
        private TextView errTitle;

        public EmptyHolder(View itemView) {
            super(itemView);
            errTitle = (TextView) itemView.findViewById(R.id.tvExceptionHint);
        }
    }

    class HallBanner extends RecyclerView.ViewHolder {
        private HeadLinesView hlvHallBanner;

        public HallBanner(View itemView) {
            super(itemView);
            hlvHallBanner = (HeadLinesView) itemView.findViewById(R.id.hlvHallBanner);
        }
    }
}
