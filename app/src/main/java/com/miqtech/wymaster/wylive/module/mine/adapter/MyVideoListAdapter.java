package com.miqtech.wymaster.wylive.module.mine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.entity.LiveInfo;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import java.util.List;

/**
 * 我的视频adapter
 * Created by wuxn on 2016/8/25.
 */
public class MyVideoListAdapter extends BaseAdapter {
    private List<LiveInfo> lives;
    private Context context;


    public MyVideoListAdapter(Context context, List<LiveInfo> lives) {
        this.context = context;
        this.lives = lives;
    }

    @Override
    public int getCount() {
        return lives.size();
    }

    @Override
    public Object getItem(int position) {
        return lives.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewHolder holder ;
        if(v == null){
            holder = new ViewHolder();
            v = View.inflate(context,R.layout.layout_video_item,null);
            holder.ivVideoPic = (ImageView)v.findViewById(R.id.ivVideoPic);
            holder.tvVideoTitle = (TextView)v.findViewById(R.id.tvVideoTitle);
            holder.tvVideoContent = (TextView)v.findViewById(R.id.tvVideoContent);
            holder.tvPlayNum = (TextView)v.findViewById(R.id.tvPlayNum);
            holder.tvCommentNum = (TextView)v.findViewById(R.id.tvCommentNum);
            holder.llHandle = (LinearLayout)v.findViewById(R.id.llHandle);
            holder.llVideoItem = (LinearLayout)v.findViewById(R.id.llVideoItem);
            v.setTag(holder);
        }else{
            holder = (ViewHolder)v.getTag();
        }
        LiveInfo live = lives.get(position);
        AsyncImage.displayImage(live.getIcon(),holder.ivVideoPic,R.drawable.default_img);
        holder.tvVideoTitle.setText(live.getTitle());
        holder.tvVideoContent.setText(live.getNickname());
        holder.tvPlayNum.setText(live.getPlayTimes()+"");
  //      holder.tvCommentNum.setText(live.getCommentNum()+"");
        return v;
    }

    private class ViewHolder {
        ImageView ivVideoPic;
        TextView tvVideoTitle;
        TextView tvVideoContent;
        LinearLayout llVideoItem;
        TextView tvPlayNum;
        TextView tvCommentNum;
        LinearLayout llHandle;
    }
}
