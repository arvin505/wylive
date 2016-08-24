package com.miqtech.wymaster.wylive.module.search.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.utils.L;
import com.miqtech.wymaster.wylive.utils.PreferenceUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;


/**
 * Created by xiaoyi on 2016/8/23.
 */
@Title(title = "搜索")
@LayoutId(R.layout.activity_search)
public class SearchActivity extends BaseAppCompatActivity implements AdapterView.OnItemClickListener {
    private final String SEARCH_HISTORY = "SEARCH_HISTORY";
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.lv_history)
    ListView lvHistory;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    saveSearchContent(v.getText().toString());
                }
                return false;
            }
        });

        L.e(TAG, "------list------" + getSearchHistory());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getSearchHistory());
        TextView tv = new TextView(this);

        tv.setText("搜索历史");
        lvHistory.addHeaderView(tv);
        lvHistory.setAdapter(adapter);
        lvHistory.setOnItemClickListener(this);

    }

    /**
     * 存储搜索内容
     */
    private void saveSearchContent(String content) {
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
            return Arrays.asList(history);
        } else {
            return Arrays.asList(history.split("##"));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putString("key", getSearchHistory().get(position - 1));
        jumpToActivity(SearchContentActivity.class, bundle);
        showToast("click : " + position);
    }
}
