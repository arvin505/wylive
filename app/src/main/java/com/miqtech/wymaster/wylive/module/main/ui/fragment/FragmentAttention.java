package com.miqtech.wymaster.wylive.module.main.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.annotations.Title;
import com.miqtech.wymaster.wylive.base.BaseFragment;
import com.miqtech.wymaster.wylive.common.FragmentPagerAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xiaoyi on 2016/8/22.
 * 关注
 */
@LayoutId(R.layout.fragment_attention)
@Title(title = "关注")
public class FragmentAttention extends BaseFragment {
    @BindView(R.id.btn_atten_anchor)
    Button btnAttenAnchor;
    @BindView(R.id.btn_atten_game)
    Button btnAttenGame;
    @BindView(R.id.vp_atten_content)
    ViewPager vpAttenContent;
    FragmentPagerAdapter mAdapter;

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        mAdapter = new FragmentPagerAdapter(mActivity);
        mAdapter.addTab(FragmentAttentionAnchor.class, null);
        mAdapter.addTab(FragmentAttentionGame.class, null);

        vpAttenContent.setAdapter(mAdapter);
        vpAttenContent.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                changeState(position   );
            }
        });
    }


    @OnClick({R.id.btn_atten_anchor, R.id.btn_atten_game})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_atten_anchor:
                changeState(0);
                break;
            case R.id.btn_atten_game:
                changeState(1);
                break;
        }
    }

    private void changeState(int position) {
        vpAttenContent.setCurrentItem(position, true);
        if (position == 0) {
            btnAttenAnchor.setBackgroundResource(R.drawable.bg_anchor_selected);
            btnAttenGame.setBackgroundResource(R.drawable.bg_game_unselected);
            btnAttenAnchor.setTextColor(mActivity.getResources().getColor(R.color.white));
            btnAttenGame.setTextColor(mActivity.getResources().getColor(R.color.bar_text_selected));
        } else {
            btnAttenAnchor.setBackgroundResource(R.drawable.bg_anchor_unselected);
            btnAttenGame.setBackgroundResource(R.drawable.bg_game_selected);
            btnAttenAnchor.setTextColor(mActivity.getResources().getColor(R.color.bar_text_selected));
            btnAttenGame.setTextColor(mActivity.getResources().getColor(R.color.white));
        }
    }
}
