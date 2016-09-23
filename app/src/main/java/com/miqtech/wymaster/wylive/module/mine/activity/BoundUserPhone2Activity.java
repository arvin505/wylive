package com.miqtech.wymaster.wylive.module.mine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;

import butterknife.BindView;


/**
 * 绑定手机2
 * Created by wuxn on 2016/8/25.
 */
@LayoutId(R.layout.activity_bounduserphone2)
public class BoundUserPhone2Activity extends BaseAppCompatActivity implements View.OnClickListener{
    @BindView(R.id.tvPhoneNum)
    TextView tvPhoneNum;
    @BindView(R.id.edtPhoneNum)
    EditText edtPhoneNum;
    @BindView(R.id.tvCountDown)
    TextView tvCountDown;
    @BindView(R.id.btnBound)
    Button btnBound;

    private String phoneNum;



    @Override
    protected void initViews(Bundle savedInstanceState) {
        phoneNum = getIntent().getStringExtra("phoneNum");
        tvCountDown.setOnClickListener(this);
        btnBound.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBound:
                Intent intent = new Intent();
                intent.putExtra("phoneNum",phoneNum);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }


}
