package com.miqtech.wymaster.wylive.module.live.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.entity.FirstCommentDetail;
import com.miqtech.wymaster.wylive.entity.SecondCommentDetail;
import com.miqtech.wymaster.wylive.entity.User;
import com.miqtech.wymaster.wylive.entity.UserImage;
import com.miqtech.wymaster.wylive.module.comment.PersonalHomePhotoActivity;
import com.miqtech.wymaster.wylive.proxy.UserProxy;
import com.miqtech.wymaster.wylive.utils.DateUtils;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.Utils;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 直播和视频播放里面的评论adapter
 * <p/>
 * Created by Administrator on 2015/11/30 0030.
 */
public class RecreationCommentAdapter extends BaseAdapter {
    private Context context;
    private List<FirstCommentDetail> comments;
    private DisplayMetrics displayMetrics;
    private Display display;
    private int useId;
    private User user;

    private int current;//用来计数回复的条数
    private TextView tvReplyContent;
    private TextView tvReplytime;
    private LinearLayout reply_reply_comment_ll_item;
    private TextView tvDelete;

    private String replyColon = " 回复 ";
    private String colon = " : ";

    public RecreationCommentAdapter(Context context, List<FirstCommentDetail> comments) {
        this.context = context;
        this.comments = comments;
        user = UserProxy.getUser();
        if (user != null) {
            useId = Integer.parseInt(user.getId());
        }
    }
    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = View.inflate(context, R.layout.layout_comment_match_item2, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
            holder.comment_detail.setVisibility(View.VISIBLE);
            holder.topTitle.setVisibility(View.GONE);
            holder.buttomTitle.setVisibility(View.GONE);
            holder.viewLine.setVisibility(View.VISIBLE);
            holder.llSeeMore.setVisibility(View.GONE);
            holder.rlTopTitle.setVisibility(View.GONE);
            showCommentData(position, holder);
        return view;
    }

    /**
     * 显示评论数据
     *
     * @param position 位置
     * @param myHolder
     */
    private void showCommentData(int position, final ViewHolder myHolder) {
        if (comments.isEmpty()) {
            return;
        }
        FirstCommentDetail bean = comments.get(position);
        if(bean==null){
            return;
        }
        String strDate;

        //显示头像
        if (!TextUtils.isEmpty(bean.getIcon())) {
            AsyncImage.displayImage(bean.getIcon(), myHolder.ivUserHeader , R.drawable.default_head);
        } else {
            myHolder.ivUserHeader.setImageResource(R.drawable.default_head);
        }

        //显示昵称
        if (!TextUtils.isEmpty(bean.getNickname())) {
            myHolder.tvUserName.setText(bean.getNickname());
        } else {
            myHolder.tvUserName.setText("");
        }

        //显示评论内容
        if (!TextUtils.isEmpty(bean.getContent())) {
            myHolder.tvContent.setText(bean.getContent());
        } else {
            myHolder.tvContent.setText("");
        }

        //显示时间
        if (!TextUtils.isEmpty(bean.getCreateDate())) {

            strDate = DateUtils.friendlyTime(bean.getCreateDate());
            myHolder.tvTime.setText(strDate);
        } else {
            myHolder.tvTime.setText("");
        }

        //判断是否显示删除按钮
        if (user != null && user.getId().equals(bean.getUserId() + "")) {
            myHolder.tvDelect.setVisibility(View.VISIBLE);
        } else {
            myHolder.tvDelect.setVisibility(View.INVISIBLE);
        }

        //显示是否评论的状态
        if (bean.getIsPraise() == 0) {
            myHolder.praiseIv.setImageResource(R.drawable.comment_praise_no);
        } else if (bean.getIsPraise() == 1) {
            myHolder.praiseIv.setImageResource(R.drawable.comment_praise_yes);
        }

        //显示点赞数
        myHolder.praiseTv.setText(Utils.calculate(bean.getLikeCount(),10000,"W"));
        if (comments.isEmpty() || comments.get(position).getReplyList() == null || comments.get(position).getReplyList().isEmpty()) {
             myHolder.replyReplyLl.setVisibility(View.GONE);
        }else {
            myHolder.replyReplyLl.setVisibility(View.VISIBLE);
            AddReplyReplyView(myHolder.replyReplyLl, position);
        }

        if(!TextUtils.isEmpty(bean.getImg())) {
            L.e("RecreationCommentAdapter","不为空");
            myHolder.ivCommentImg.setVisibility(View.VISIBLE);
            L.e("RecreationCommentAdapter","不为空::::::"+bean.getImg());
            AsyncImage.displayImageWithCallback(bean.getImg(), myHolder.ivCommentImg, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    L.e("RecreationCommentAdapter","onLoadingStarted");
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    L.e("RecreationCommentAdapter","onLoadingFailed");
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    L.e("RecreationCommentAdapter","onLoadingComplete");
                    Drawable drawable = new BitmapDrawable(loadedImage);
                    float viewWidth = DeviceUtils.dp2px(context, 100);
                    int height = drawable.getIntrinsicHeight();
                    int width = drawable.getIntrinsicWidth();
                    float scale = (float) height / width;
                    ViewGroup.LayoutParams lp = view.getLayoutParams();
                    lp.width = (int) viewWidth;
                    lp.height = (int) ((int) viewWidth * scale);
                    view.setLayoutParams(lp);
                    L.e("RecreationCommentAdapter",viewWidth+":::"+lp.width+":::"+lp.height+":::"+scale+":::"+height+"::::"+width);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
        }else {
            L.e("RecreationCommentAdapter","空");
            myHolder.ivCommentImg.setVisibility(View.GONE);
        }

        //设置监听事件
        MyOnCliclLister myOnCliclLister = new MyOnCliclLister(position, myHolder);
        myHolder.replyLl.setOnClickListener(myOnCliclLister);//回复
        myHolder.tvDelect.setOnClickListener(myOnCliclLister);//删除
        myHolder.praiseLl.setOnClickListener(myOnCliclLister);//赞
        myHolder.ivUserHeader.setOnClickListener(myOnCliclLister);//头像
        myHolder.tvContent.setOnClickListener(myOnCliclLister); //点击文字区域 跳到详情
        myHolder.replyReplyLl.setOnClickListener(myOnCliclLister);
        myHolder.ivCommentImg.setOnClickListener(myOnCliclLister); //图片点击放大
    }

    /**
     * 添加楼中楼的评论
     *
     * @param linearLayout
     * @param position     recyclerview中的位置
     */
    private void AddReplyReplyView(LinearLayout linearLayout, int position) {
        linearLayout.removeAllViews();
        if (comments.isEmpty() || comments.get(position).getReplyList() == null || comments.get(position).getReplyList().isEmpty() || linearLayout == null) {
            return;
        }
        //5条以内，不显示“还有更多”，否则显示
        if (comments.get(position).getReplyCount() < 6) {
            current = comments.get(position).getReplyList().size();
        } else {
            current = 6;
        }

        for (int i = 0; i < current; i++) {
            linearLayout.addView(creatReplyReplyView(i, position));
        }
    }

    /**
     * 创建楼中楼
     *
     * @param isShowButtom
     * @param position     在屏幕中的位置
     * @return
     */
    private View creatReplyReplyView(final int isShowButtom, final int position) {
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.layout_item_reply_comment, null);
        reply_reply_comment_ll_item = (LinearLayout) view.findViewById(R.id.reply_reply_comment_ll_item);//最外层布局的
        tvReplyContent = (TextView) view.findViewById(R.id.comment_details_item);//显示的评论
        tvReplytime = (TextView) view.findViewById(R.id.comment_time_item);//显示的时间
        tvDelete = (TextView) view.findViewById(R.id.comment_time_delete);

        if (isShowButtom < 5) {
            if (isShowButtom < comments.get(position).getReplyList().size()) {
                SecondCommentDetail bean = comments.get(position).getReplyList().get(isShowButtom);
                tvReplyContent.setGravity(Gravity.LEFT);
                if (!TextUtils.isEmpty(bean.getNickname()) && !TextUtils.isEmpty(bean.getContent())) {
                    tvReplyContent.setText(addconnent(bean));
                    tvReplyContent.setMovementMethod(LinkMovementMethod.getInstance());
                } else {
                    tvReplyContent.setText("");
                }

                //显示时间
                if (!TextUtils.isEmpty(bean.getCreateDate())) {
                    String strDate = DateUtils.friendlyTime(bean.getCreateDate());
                    tvReplytime.setText(strDate);
                } else {
                    tvReplytime.setText("");
                }

                if (user != null && useId == bean.getUserId()) {
                    tvDelete.setVisibility(View.VISIBLE);
                    tvDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemDataDealWith.delectReplyReply(position, isShowButtom);
                        }
                    });
                } else {
                    tvDelete.setVisibility(View.GONE);
                }
            }
        } else {
            tvReplyContent.setText("更多" + Utils.calculate((comments.get(position).getReplyCount() - 5), 10000,"W") + "条回复。。。");
            LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) tvReplyContent.getLayoutParams();
            lp.gravity=Gravity.RIGHT;
            lp.bottomMargin=DeviceUtils.dp2px(context,9);
            lp.rightMargin=DeviceUtils.dp2px(context,30);
            tvReplytime.setVisibility(View.GONE);
            tvReplyContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemDataDealWith.lookComment(position);
                }
            });
        }

        return view;
    }

    /**
     * 显示楼中楼
     * 当replyName为空时表示回复楼主，不为空时表示回复层主
     *
     * @return 拼接好的内容
     */
    private SpannableStringBuilder addconnent(final SecondCommentDetail bean) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(bean.getNickname());
        int start = bean.getNickname().length();
        ssb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
//                Intent intent = new Intent(context, PersonalHomePageActivity.class);
//                intent.putExtra("id", bean.getUserId() + "");
//                context.startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(context.getResources().getColor(R.color.category_item_count));// 设置文本颜色
                // 去掉下划线
                ds.setUnderlineText(false);
            }
        }, 0, start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (bean.getParent_id() != bean.getReply_id() && !TextUtils.isEmpty(bean.getReplyname())) {
            int end = start + replyColon.length() + bean.getReplyname().length();
            ssb.append(replyColon + bean.getReplyname());

            ssb.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
//                    Intent intent = new Intent(context, PersonalHomePageActivity.class);
//                    intent.putExtra("id", bean.getReplyUserId() + "");
//                    context.startActivity(intent);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.category_item_count));// 设置文本颜色
                    // 去掉下划线
                    ds.setUnderlineText(false);
                }
            }, start + replyColon.length(), end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        return ssb.append(colon + bean.getContent());
    }

    private class MyOnCliclLister implements View.OnClickListener {
        int position;
        ViewHolder viewHolder;

        public MyOnCliclLister(int position, ViewHolder viewHolder) {
            this.position = position;
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.reply_comment_ll_item://回复  回复楼主
                    itemDataDealWith.replyComment(position);
                    break;
                case R.id.tvContent://点击文字区域跳转  单个评论详情
                    itemDataDealWith.lookComment(position);
                    break;
                case R.id.reply_reply_comment_ll_item://点击楼中楼  单个评论详情
                    itemDataDealWith.lookComment(position);
                    break;
                case R.id.tvDelect://删除评论
                    itemDataDealWith.delect(position);
                    break;
                case R.id.praise_comment_ll_item://点赞
                    itemDataDealWith.praiseComment(position);
                    break;
                case R.id.ivUserHeader://点击头像跳转
//                    intent = new Intent(context, PersonalHomePageActivity.class);
//                    intent.putExtra("id", comments.get(position).getUserId() + "");
//                    context.startActivity(intent);
                    break;
                case R.id.ivCommentImg:
                    intent = new Intent();
                    Bundle bundle = new Bundle();
                    ArrayList<UserImage> userPhotos = new ArrayList<UserImage>();
                    UserImage userImage=new UserImage();
                    userImage.setImg(comments.get(position).getImg());
                    userPhotos.add(userImage);
                    bundle.putSerializable("images", userPhotos);
                    bundle.putInt("image_index", 0);
                    bundle.putBoolean("isFromComment", true);
                    intent.putExtras(bundle);
                    intent.setClass(context, PersonalHomePhotoActivity.class);
                    context.startActivity(intent);
                    break;
            }
        }
    }


    static class ViewHolder {
        @BindView(R.id.ivUserHeader)
        CircleImageView ivUserHeader;
        @BindView(R.id.tvUserName)
        TextView tvUserName;
        @BindView(R.id.tvContent)
        TextView tvContent;
        @BindView(R.id.tvTime)
        TextView tvTime;
        @BindView(R.id.tvDelect)
        TextView tvDelect;

        @BindView(R.id.comment_top_title_tv_item)
        TextView topTitle;//顶部的最新评论
        @BindView(R.id.comment_buttom_tv_item)
        TextView buttomTitle;//底部的查看更多
        @BindView(R.id.praise_comment_iv_item)
        ImageView praiseIv;//点赞
        @BindView(R.id.praise_comment_tv_item)
        TextView praiseTv;//点赞
        @BindView(R.id.praise_comment_ll_item)
        LinearLayout praiseLl;//点赞

        @BindView(R.id.reply_comment_ll_item)
        LinearLayout replyLl;//回复

        @BindView(R.id.reply_reply_comment_ll_item)
        LinearLayout replyReplyLl;//楼中楼
        @BindView(R.id.comment_detail_ll_item)
        LinearLayout comment_detail;//评论详情
        @BindView(R.id.line_view_comment_item)
        View viewLine;//线

        @BindView(R.id.llSeeMore)
        LinearLayout llSeeMore;//最底部查看更多
        @BindView(R.id.rlTopTitle)
        RelativeLayout rlTopTitle;//最顶部的最新评论标题
        @BindView(R.id.ivCommentImg)
        ImageView ivCommentImg; //评论中图片

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public interface ItemDataDealWith {
        /**
         * 删除
         *
         * @param position
         */
        void delect(int position);

        /**
         * 点赞或者取消
         *
         * @param position 该条评论的位置
         */
        void praiseComment(int position);

        /**
         * 回复楼主
         *
         * @param position 该条评论的位置
         */
        void replyComment(int position);

        /**
         * 查看评论
         *
         * @param position 该条评论的位置
         */
        void lookComment(int position);

        /**
         * 查看评论区
         */
        void lookCommentSection();

        /**
         * 删除楼中楼
         *
         * @param position    listview中的位置
         * @param replyListid 楼中楼的位置
         */
        void delectReplyReply(int position, int replyListid);
    }

    public void setReport(ItemDataDealWith itemDataDealWith) {
        this.itemDataDealWith = itemDataDealWith;
    }

    private ItemDataDealWith itemDataDealWith;
}
