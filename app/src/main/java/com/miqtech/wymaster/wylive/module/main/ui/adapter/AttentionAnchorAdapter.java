package com.miqtech.wymaster.wylive.module.main.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.common.BaseRecycleViewAdapter;
import com.miqtech.wymaster.wylive.entity.AnchorInfo;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;
import com.miqtech.wymaster.wylive.widget.CircleImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by xiaoyi on 2016/8/22.
 */
public class AttentionAnchorAdapter extends BaseRecycleViewAdapter {
    private Context mContext;
    private List<AnchorInfo> mDatas;
    private LayoutInflater mInflater;

    private static final int TYPE_ITEM = 0;


    public AttentionAnchorAdapter(Context context, List<AnchorInfo> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = null;
        ItemHolder holder = null;
        switch (viewType) {
            case TYPE_ITEM:
                convertView = mInflater.inflate(R.layout.layout_attention_anchor_item, parent, false);
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

    private void initItemView(final ItemHolder holder, final int position) {
        AnchorInfo anchorInfo = mDatas.get(position);
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

    static class ItemHolder extends RecyclerView.ViewHolder {
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

        public ItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
