package com.miqtech.wymaster.wylive.module.search.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;

import java.util.Date;
import java.util.List;

/**
 * Created by xiaoyi on 2016/8/24.
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mDatas;
    private LayoutInflater mInflater;

    public SearchHistoryAdapter(Context mContext, List<String> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        if (mDatas == null) return 0;
        if (mDatas.size()>5)return 5;
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.layout_search_history_item, parent, false);
        }
        String data = mDatas.get(position);
        TextView tv = (TextView) convertView.findViewById(R.id.tv_history);
        tv.setText(data);
        convertView.findViewById(R.id.img_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onDelete(v, position);
                }
            }
        });
        return convertView;
    }

    public interface OnDeleteClick {
        void onDelete(View view, int position);
    }

    private OnDeleteClick mListener;

    public void setItemDeleteListener(OnDeleteClick mListener) {
        this.mListener = mListener;
    }
}
