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
import cst.kop.tools.TwoDialogCallback;

/**
 * Created by zhou-pc on 2016/9/27.
 */
public class TwoEditTextDialog extends Dialog {

    Button positiveButton;
    Button negativeButton;
    EditText appIdTv;
    EditText appSecTv;
    TextView titleTv;
    TwoDialogCallback mCallback;

    public TwoEditTextDialog(Context context, int themeResId, TwoDialogCallback callback) {
        super(context, themeResId);
        mCallback = callback;
    }

    public TwoEditTextDialog(Context context, TwoDialogCallback callback) {
        super(context);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mCallback = callback;
    }

    public TwoEditTextDialog(Context context, boolean cancelable, OnCancelListener cancelListener, TwoDialogCallback callback) {
        super(context, cancelable, cancelListener);
        mCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cst_widget_edit_dialog);
        Button ok = (Button) findViewById(R.id.alert_dialog_confirm_btn);
        ok.setVisibility(View.GONE);
        titleTv = (TextView) findViewById(R.id.alert_dialog_title_tv);
        appIdTv = (EditText) findViewById(R.id.alert_dialog_appid_edtv);
        appSecTv = (EditText) findViewById(R.id.alert_dialog_appsec_edtv);
        positiveButton = (Button) findViewById(R.id.alert_dialog_left_btn);
        negativeButton = (Button) findViewById(R.id.alert_dialog_right_btn);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mCallback)
                    mCallback.OnPositiveBtnClick(appIdTv.getText().toString(), appSecTv.getText().toString());
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mCallback)
                    mCallback.OnNegativeBtnClick(TwoEditTextDialog.this);
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


    public void setTextHint(String leftBtnText, String rightBtnText) {
        appIdTv.setHint(leftBtnText);
        appSecTv.setHint(rightBtnText);
    }

    public void setDialogTitle(String title) {
        if (null != titleTv)
            titleTv.setText(title);
    }


}
