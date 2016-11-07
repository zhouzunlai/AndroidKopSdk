package platform.cston.explain.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cston.cstonlibray.R;


public class BlockDialog extends Dialog {

    private static int default_width = 150; // 默认宽度

    private static int default_height = 80;// 默认高度

    //加载视图
    LoadingView loadingV;

    TextView mLoadingTextView;

    Context context;

    // 设置默认高度为160，宽度120，并且可根据屏幕像素密度自动进行大小调整
    public BlockDialog(Context context) {
        this(context, default_width, default_height,
                R.layout.cst_platform_dialog_block, R.style.cst_platform_Theme_dialog);
        setCanceledOnTouchOutside(false);
    }

    public BlockDialog(Context context, String message) {
        this(context, default_width, default_height,
                R.layout.cst_platform_dialog_block, R.style.cst_platform_Theme_dialog);
        setCanceledOnTouchOutside(false);
        setText(message);
    }

    public BlockDialog(Context context, boolean outCancle) {
        this(context, default_width, default_height,
                R.layout.cst_platform_dialog_block, R.style.cst_platform_Theme_dialog);
        setCanceledOnTouchOutside(outCancle);// 设置点击周围会不会消失, false 不消失
    }

    public BlockDialog(Context context, int width, int height, int layout,
                       int style) {
        super(context, style);
        this.context = context;
        setContentView(layout);
        loadingV = (LoadingView) findViewById(R.id.loading_v);
        loadingV.startLoading();
        mLoadingTextView = (TextView) findViewById(R.id.progress_dialog_tv);

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
                mLoadingTextView.setVisibility(View.GONE);
            } else {
                mLoadingTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    private float getDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    @Override
    public void show() {
        if (context != null && !((Activity) context).isFinishing()) {
            super.show();
        }
    }
}
