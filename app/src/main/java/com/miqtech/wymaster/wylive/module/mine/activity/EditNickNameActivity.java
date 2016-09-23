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
import com.miqtech.wymaster.wylive.utils.ToastUtils;

import butterknife.BindView;

/**
 * 修改昵称
 * Created by wuxn on 2016/8/28.
 */
@LayoutId(R.layout.activity_editnickname)
public class EditNickNameActivity extends BaseAppCompatActivity implements View.OnClickListener{
    @BindView(R.id.edtNickName)
    EditText edtNickName;
    @BindView(R.id.btnSave)
    Button btnSave;

    private String nickName;

    private Context context ;

    @Override
    protected void initViews(Bundle savedInstanceState) {
        nickName = getIntent().getStringExtra("nickName");
        context = this;
        if(!TextUtils.isEmpty(nickName)){
            edtNickName.setText(nickName);
            if(nickName.length()>20){
                edtNickName.setSelection(20);
            }else{
                edtNickName.setSelection(nickName.length());
            }
        }
        setTitle("昵称");
        btnSave.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                if(!TextUtils.isEmpty(edtNickName.getText().toString())){
                    Intent intent = new Intent();
                    intent.putExtra("nickName", edtNickName.getText().toString());
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    ToastUtils.show("请输入一个不为空的昵称");
                }
                break;
        }
    }
}
