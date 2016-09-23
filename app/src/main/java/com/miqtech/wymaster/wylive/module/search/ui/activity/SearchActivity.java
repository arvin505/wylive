package com.miqtech.wymaster.wylive.module.search.ui.activity;


import android.os.Bundle;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.common.SimpleTextWatcher;
import com.miqtech.wymaster.wylive.module.search.ui.adapter.SearchHistoryAdapter;
import com.miqtech.wymaster.wylive.utils.DeviceUtils;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by xiaoyi on 2016/8/23.
 */
@LayoutId(R.layout.activity_search)
public class SearchActivity extends BaseAppCompatActivity implements AdapterView.OnItemClickListener, SearchHistoryAdapter.OnDeleteClick {
    private final String SEARCH_HISTORY = "SEARCH_HISTORY";
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_history)
    ListView lvHistory;

    SearchHistoryAdapter adapter;

    List<String> mDatas;
    View footerView;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    saveSearchContent(v.getText().toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("key", v.getText().toString());
                    jumpToActivity(SearchContentActivity.class, bundle);
                }
                return false;
            }
        });

        L.e(TAG, "------list------" + getSearchHistory());
        mDatas = getSearchHistory();
        TextView header = new TextView(this);
        header.setTextSize(10);
        header.setText("搜索历史");
        header.setTextColor(getResources().getColor(R.color.bar_text_selected));
        header.setPadding(0, DeviceUtils.dp2px(this, 15), 0, DeviceUtils.dp2px(this, 10));
        lvHistory.addHeaderView(header);

        adapter = new SearchHistoryAdapter(this, mDatas);
        lvHistory.setAdapter(adapter);
        lvHistory.setOnItemClickListener(this);
        adapter.setItemDeleteListener(this);
        addFooter();

        etSearch.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                lvHistory.setVisibility(View.GONE);
            }
        });
    }

    private void addFooter() {
        if (mDatas == null || mDatas.isEmpty()) return;
        footerView = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.RIGHT;
        TextView footer = new TextView(this);
        footer.setTag("footer");
        footer.setGravity(Gravity.CENTER);
        footer.setText("清空历史搜索");
        footer.setTextSize(12);
        footer.setTextColor(getResources().getColor(R.color.bar_text_selected));
        footer.setPadding(0, DeviceUtils.dp2px(this, 15), 0, DeviceUtils.dp2px(this, 10));
        ((ViewGroup) footerView).addView(footer, params);

        lvHistory.addFooterView(footerView);
        footerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatas.clear();
                PreferenceUtils.putString(SEARCH_HISTORY, "");
                adapter.notifyDataSetChanged();
                lvHistory.removeFooterView(footerView);
            }
        });
    }

    /**
     * 存储搜索内容
     */
    private void saveSearchContent(String content) {
        if (content == null || content.trim().equals("")) return;
        List history = getSearchHistory();
        if (history.contains(content)) return;
        StringBuilder sb = new StringBuilder(PreferenceUtils.getString(SEARCH_HISTORY));
        if (sb.toString().equals("")) {
            sb.append(content);
            PreferenceUtils.putString(SEARCH_HISTORY, sb.toString());
        } else {
            sb.append("##").append(content);
            PreferenceUtils.putString(SEARCH_HISTORY, sb.toString());
        }
    }

    /**
     * 获取搜索历史
     */
    private List<String> getSearchHistory() {
        String history = PreferenceUtils.getString(SEARCH_HISTORY);
        if (history.equals("")) {
            return Collections.EMPTY_LIST;
        } else if (!history.contains("##")) {
            return new ArrayList<>(Arrays.asList(history));
        } else {
            List<String> result = new ArrayList<>(Arrays.asList(history.split("##")));
            Collections.reverse(result);
            return result;
        }
    }

    private void deleteHistory(String content) {
        List<String> history = getSearchHistory();
        if (history == null || !history.contains(content)) {
            return;
        }
        history.remove(content);
        if (history.size() == 0) {
            PreferenceUtils.putString(SEARCH_HISTORY, "");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String str : history) {
            sb.append(str).append("##");
        }
        String historyStr = sb.toString().substring(0, sb.toString().length() - 2);
        PreferenceUtils.putString(SEARCH_HISTORY, historyStr);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        if (position == lvHistory.getChildCount() - 1) {
            mDatas.clear();
            PreferenceUtils.putString(SEARCH_HISTORY, "");
            adapter.notifyDataSetChanged();
            lvHistory.removeFooterView(footerView);
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key", getSearchHistory().get(position - 1));
        jumpToActivity(SearchContentActivity.class, bundle);
    }

    @OnClick({R.id.tv_cancle})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancle:
                finish();
                break;
        }
    }

    @Override
    public void onDelete(View view, int position) {
        deleteHistory(mDatas.get(position));
        mDatas.remove(position);
        if (adapter.getCount() == 0) {
            //lvHistory.findViewWithTag("footer").setVisibility(View.GONE);
            lvHistory.removeFooterView(footerView);
        }
        adapter.notifyDataSetChanged();

        showToast(adapter.getCount() + "  count");
    }
}
