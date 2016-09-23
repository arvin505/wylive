package com.miqtech.wymaster.wylive.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.miqtech.wymaster.wylive.R;

/**
 * Created by admin on 2016/9/13.
 */
public class CustomDialog extends AlertDialog {
    private Window window = null;

    /***
     *
     * @param context
     * @param contentView
     *            配置文件
     *            集合
     */
    public CustomDialog(final Context context, View contentView) {
        super(context);
        if (!isShowing()) {
            show();
        }
        window = this.getWindow();
        setCancelable(false);
        setCanceledOnTouchOutside(true);
        window.setWindowAnimations(R.style.dialogWindowAnim); // 设置窗口弹出动画
        WindowManager.LayoutParams params =
                window.getAttributes();
        params.width = (int) (((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()
                / 10f * 8);
        window.setGravity(Gravity.CENTER);
        window.setBackgroundDrawableResource(android.R.color.transparent); // 设置对话框背景为透明
        window.setAttributes(params);
        window.setContentView(contentView);
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
    }
}
