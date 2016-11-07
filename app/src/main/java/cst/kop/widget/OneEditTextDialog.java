package cst.kop.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cst.kop.R;
import cst.kop.tools.OneDialogCallback;

/**
 * Created by zhou-pc on 2016/9/27.
 */
public class OneEditTextDialog extends Dialog {

    Button positiveButton;
    Button negativeButton;
    EditText appIdTv;
    TextView titleTv;
    TextView titleTip;
    OneDialogCallback mCallback;

    public OneEditTextDialog(Context context, int themeResId, OneDialogCallback callback) {
        super(context, themeResId);
        mCallback = callback;
    }

    public OneEditTextDialog(Context context, OneDialogCallback callback) {
        super(context);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mCallback = callback;
    }

    public OneEditTextDialog(Context context, boolean cancelable, OnCancelListener cancelListener, OneDialogCallback callback) {
        super(context, cancelable, cancelListener);
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cst_widget_one_edit_dialog);

        Button ok = (Button) findViewById(R.id.alert_dialog_confirm_btn);
        ok.setVisibility(View.GONE);
        titleTv = (TextView) findViewById(R.id.alert_dialog_title_tv);
        titleTip = (TextView) findViewById(R.id.alert_tip);
        appIdTv = (EditText) findViewById(R.id.alert_one_dialog_appid_edtv);
        positiveButton = (Button) findViewById(R.id.alert_dialog_left_btn);
        negativeButton = (Button) findViewById(R.id.alert_dialog_right_btn);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mCallback)
                    mCallback.OnPositiveBtnClick(appIdTv.getText().toString());
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mCallback)
                    mCallback.OnNegativeBtnClick(OneEditTextDialog.this);
            }
        });
        setDialogHeightAndWidth();
    }

    /*
     * 将对话框的大小按屏幕大小的百分比设置
     */
    private void setDialogHeightAndWidth() {
        WindowManager m = (WindowManager) getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.9); // 宽度设置为屏幕的0.8
        this.getWindow().setAttributes(p);
    }


    public void setTextHint(String leftBtnText) {
        appIdTv.setHint(leftBtnText);
    }

    public void setDialogTitleAndTip(String title, String Tip) {
        if (null != titleTv)
            titleTv.setText(title);
        if (titleTip != null)
            titleTip.setText(Tip);
    }


}
