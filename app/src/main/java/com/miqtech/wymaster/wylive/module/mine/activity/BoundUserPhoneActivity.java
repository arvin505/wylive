package com.miqtech.wymaster.wylive.module.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.annotations.LayoutId;
import com.miqtech.wymaster.wylive.base.BaseAppCompatActivity;
import com.miqtech.wymaster.wylive.constants.Constants;
import com.miqtech.wymaster.wylive.utils.ToastUtils;

import butterknife.BindView;

/**
 * 绑定手机
 * Created by wuxn on 2016/8/24.
 */
@LayoutId(R.layout.activity_bounduserphone)
public class BoundUserPhoneActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @BindView(R.id.edtPhoneNum)
    EditText edtPhoneNum;
    @BindView(R.id.btnSave)
    Button btnSave;

    private Context context;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitle("绑定手机");
        context = this;
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                String telephone = edtPhoneNum.getText().toString();
                if (!TextUtils.isEmpty(telephone) && telephone.matches(Constants.PHONE_FORMAT)) {
                    Intent intent = new Intent();
                    intent.setClass(context, BoundUserPhone2Activity.class);
                    intent.putExtra("phoneNum", telephone);
                    startActivityForResult(intent, EditUserInfoActviity.REQUEST_PHONENUM);
                } else {
                    ToastUtils.show("请输入正确格式的手机号码");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EditUserInfoActviity.REQUEST_PHONENUM && resultCode == RESULT_OK && data != null) {
            Intent intent = new Intent();
            intent.putExtra("phoneNum", data.getStringExtra("phoneNum"));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
