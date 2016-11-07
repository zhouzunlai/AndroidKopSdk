/*
 * Copyright (c) 2016.  CST.All Rights Reserved
 *
 * @author:zhouzunlai
 *
 * @date: 2016.5.7.
 *
 */

package platform.cston.explain.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cston.cstonlibray.R;
import platform.cston.explain.utils.CstPlatformUtils;


public class CstLoadDialog extends Dialog {

    private CstLoadingView mLoadingView; //加载视图

    private TextView mLoadingTextView;

    private Context context;

    // 设置默认高度为150，宽度80，并且可根据屏幕像素密度自动进行大小调整
    public CstLoadDialog(Context context) {
        this(context, 150, 80,
                R.layout.cst_platform_widget_load_dialog, R.style.Cst_Platform_Style_Load_dialog);
        setCanceledOnTouchOutside(false);
    }

    public CstLoadDialog(Context context, String message) {
        this(context, 150, 80,
                R.layout.cst_platform_widget_load_dialog, R.style.Cst_Platform_Style_Load_dialog);
        setCanceledOnTouchOutside(false);
        setText(message);
    }

    public CstLoadDialog(Context context, boolean outCancle) {
        this(context, 150, 80,
                R.layout.cst_platform_widget_load_dialog, R.style.Cst_Platform_Style_Load_dialog);
        setCanceledOnTouchOutside(outCancle);// 设置点击周围会不会消失, false 不消失
    }

    public CstLoadDialog(Context context, int width, int height, int layout,
                         int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        mLoadingView = (CstLoadingView) findViewById(R.id.cst_platform_view_loading);
        mLoadingView.startLoading();
        mLoadingTextView = (TextView) findViewById(R.id.cst_platform_text_loading);

        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        float density = getDensity(context);
        params.width = (int) (width * density);
        params.height = (int) (height * density);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
    }

    public void setText(String message) {
        if (mLoadingTextView != null) {
            mLoadingTextView.setText(message);
            if (message == null || message.isEmpty()) {
                CstPlatformUtils.gone(mLoadingTextView);
            } else {
                CstPlatformUtils.visible(mLoadingTextView);
            }
        }
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    @Override
    public void cancel() {
        super.cancel();
        mLoadingView.stopLoading();
    }


    @Override
    public void dismiss() {
        super.dismiss();
        mLoadingView.stopLoading();
    }

    @Override
    public void show() {
        if (context != null && !((Activity) context).isFinishing()) {
            super.show();
        }
    }
}
