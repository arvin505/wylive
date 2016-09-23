package com.miqtech.wymaster.wylive.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.WYLiveApp;

/**
 * 监听号码的输入
 * zhaosentao
 */
public class PhoneTextWatcher implements TextWatcher {

    private EditText _text;
    private ImageView ivClearPhone;
    private boolean isFullPhone = false;//是否输入了完整的电话号码
    private Button button;
    private TextView textView;

    /**
     * @param _text        所监听输入框
     * @param ivClearPhone
     * @param hint
     */
    public PhoneTextWatcher(final EditText _text, final ImageView ivClearPhone, final String hint, Button button,TextView textView) {
        this._text = _text;
        this.ivClearPhone = ivClearPhone;
        this.button = button;
        this.textView = textView;
        if (ivClearPhone != null) {
            ivClearPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _text.setText("");
                    _text.setHint(hint);
                    ivClearPhone.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (ivClearPhone != null) {
            if (s.toString().length() > 0) {
                ivClearPhone.setVisibility(View.VISIBLE);
            } else {
                ivClearPhone.setVisibility(View.GONE);
            }
        }

        if (s.toString().length() == 13) {
            isFullPhone = true;
            if(textView != null){
                textView.setEnabled(true);
                textView.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.attention_item_count));
            }

            if (button != null) {
                button.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.login_button));
                button.setEnabled(true);
                button.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.white));
            }
        } else {
            isFullPhone = false;
            if(textView != null){
                textView.setEnabled(false);
                textView.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.category_item_count));
            }
            if (button != null) {
                button.setEnabled(false);
                button.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.register_button));
                button.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.text_gray_one));
            }
        }

        if (s == null || s.length() == 0) return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i != 3 && i != 8 && s.charAt(i) == ' ') {
                continue;
            } else {
                sb.append(s.charAt(i));
                if ((sb.length() == 4 || sb.length() == 9) && sb.charAt(sb.length() - 1) != ' ') {
                    sb.insert(sb.length() - 1, ' ');
                }
            }
        }
        if (!sb.toString().equals(s.toString())) {
            int index = start + 1;
            if(start<sb.length()){
                if (sb.charAt(start) == ' ') {
                    if (before == 0) {
                        index++;
                    } else {
                        index--;
                    }
                } else {
                    if (before == 1) {
                        index--;
                    }
                }
            }else{
                index--;
            }
            _text.setText(sb.toString());
            _text.setSelection(index);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public boolean isFullPhone() {
        return isFullPhone;
    }

}
