package com.miqtech.wymaster.wylive.module.main.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.entity.Banner;
import com.miqtech.wymaster.wylive.module.live.LiveRoomActivity;
import com.miqtech.wymaster.wylive.module.live.PlayVideoActivity;
import com.miqtech.wymaster.wylive.module.main.ui.activity.WebViewActivitay;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;

import java.util.List;

/**
 * Created by arvin on 2015/11/15.
 */
public class ImagePagerAdapter<T> extends RecyclingPagerAdapter {

    private Context context;
    private List<T> list;

    public ImagePagerAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup container) {
        ViewHolder holder;
        final Banner banner = (Banner) list.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.headline_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.img_headlineitem_banner);
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.lable = (TextView) convertView.findViewById(R.id.tv_headlineitem_lable);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
    }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (banner.getType()) {
                   //TODO 跳转测试
                    case 1: //直播
                        intent = new Intent(context, LiveRoomActivity.class);
                        intent.putExtra("id", banner.getTarget()+"");
                        context.startActivity(intent);
                        break;
                    case 2: //视频
                        intent = new Intent(context, PlayVideoActivity.class);
                        intent.putExtra("videoId", banner.getTarget()+"");
                        context.startActivity(intent);
                        break;
                    case 3: //webview 页面
                        intent = new Intent(context, WebViewActivitay.class);
                        intent.putExtra("url", banner.getTarget());
                        context.startActivity(intent);
                        break;
                }
            }
        });
        AsyncImage.displayImage(banner.getImg(), holder.imageView);
        //图片展示逻辑
        return convertView;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView lable;
    }
}
