package com.miqtech.wymaster.wylive.widget;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miqtech.wymaster.wylive.R;
import com.miqtech.wymaster.wylive.WYLiveApp;
import com.miqtech.wymaster.wylive.constants.API;
import com.miqtech.wymaster.wylive.utils.imageloader.AsyncImage;


/**
 * @author zhangp
 */
@SuppressLint("WrongViewCast")
public class MyAlertView extends Dialog {
    public MyAlertView(Context context) {
        super(context);
    }

    public MyAlertView(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private EditText input;

        public EditText getInput() {
            return input;
        }

        public void setInput(EditText input) {
            this.input = input;
        }

        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = (String) context.getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText, OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = (String) context.getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText, OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        /**
         * 创建图片验证码
         *
         * @param mobileStr 手机号
         * @param action    dialog事件
         * @return
         */
        public MyAlertView creatImgVerificationCode(final String mobileStr, final VerificationCodeDialogAction action) {

            final MyAlertView dialog = new MyAlertView(context, R.style.register_style);
            dialog.setContentView(R.layout.dialog_register_pact_img);
            dialog.setCanceledOnTouchOutside(false);

            final EditText et_auth_code = (EditText) dialog.findViewById(R.id.dialog_register_et);
            final TextView tvYes = (TextView) dialog.findViewById(R.id.dialog_register_on_tv);
            RelativeLayout rlCode = (RelativeLayout) dialog.findViewById(R.id.dialogRlCode);
            final ImageView ivClear = (ImageView) dialog.findViewById(R.id.dialogIvClear);
            ImageView tvNo = (ImageView) dialog.findViewById(R.id.dialog_register_off_iv);
            final ImageView imgCode = (ImageView) dialog.findViewById(R.id.dialog_imageview_code_iv);

            tvYes.setEnabled(false);

            et_auth_code.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        tvYes.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.dialog_button));
                        tvYes.setEnabled(true);
                        tvYes.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.white));
                        ivClear.setVisibility(View.VISIBLE);
                    } else {
                        tvYes.setEnabled(false);
                        tvYes.setBackgroundDrawable(WYLiveApp.getContext().getResources().getDrawable(R.drawable.dialog_button_pri));
                        tvYes.setTextColor(WYLiveApp.getContext().getResources().getColor(R.color.hall_live_item_bg));
                        ivClear.setVisibility(View.GONE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.doPositive(et_auth_code.getText().toString());
                }
            });
            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    action.doNegative();
                }
            });
            imgCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncImage.loadYZM(API.HOST + API.IMAGE_CODE_REGISTER + mobileStr, imgCode);
                }
            });
            ivClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et_auth_code.setText("");
                }
            });
            action.changeView(rlCode, tvYes, imgCode, et_auth_code);
            AsyncImage.loadYZM(API.HOST + API.IMAGE_CODE_REGISTER + mobileStr, imgCode);
            dialog.show();
            return dialog;
        }

        /**
         * 创建手机已被注册的弹框
         *
         * @param action dialog事件
         * @return
         */
        public MyAlertView creatHasRegistered(final DialogAction action) {

            final MyAlertView dialog = new MyAlertView(context, R.style.register_style);
            dialog.setContentView(R.layout.dialog_has_register);
            dialog.setCanceledOnTouchOutside(false);

            Button tvYes = (Button) dialog.findViewById(R.id.dialogBtLogin);
            ImageView tvNo = (ImageView) dialog.findViewById(R.id.dialog_register_off_iv);
            TextView tvPhone = (TextView) dialog.findViewById(R.id.dialogTvOtherPhone);


            tvYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity)context).onBackPressed();
                }
            });
            tvPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    action.doPositive();
                    dialog.dismiss();
                }
            });

            tvNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    action.doNegative();
                }
            });
            dialog.show();
            return dialog;
        }
    }

    public interface DialogAction {
        void doPositive();

        void doNegative();
    }

    public interface VerificationCodeDialogAction {
        void doPositive(String verificationCode);

        void doNegative();

        /**
         * 改变空间的状态
         *
         * @param view1
         * @param view2
         * @param view3
         */
        void changeView(View view1, View view2, View view3, View view4);
    }

}
