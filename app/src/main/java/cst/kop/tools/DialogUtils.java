package cst.kop.tools;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cst.kop.R;
import cst.kop.adapter.DialogListAdapter;
import platform.cston.httplib.bean.CarListResult;

/**
 * Created by zhou-pc on 2016/9/12.
 */
public class DialogUtils {

    public static final int BUTTON1 = -1;

    public static final int BUTTON2 = -2;

    /**
     * 显示弹出列表选择项
     *
     * @param data 包含文本显示内容
     */
    public static AlertDialog showListItemChooseDialog(Context context,
                                                       List<CarListResult.CarInfo> data,
                                                       DialogListAdapter.CallBack dialogCallBack) {
        AlertDialog carListDialog = new AlertDialog.Builder(context).create();
        carListDialog.setCanceledOnTouchOutside(true);
        carListDialog.show();
        Window window = carListDialog.getWindow();
        window.setContentView(R.layout.common_list_dialog);

        ListView lv = (ListView) window.findViewById(R.id.lv);

        DialogListAdapter mDialogListAdapter = new DialogListAdapter(context, data);
        lv.setAdapter(mDialogListAdapter);

        if (dialogCallBack != null) {
            mDialogListAdapter.setCallBack(dialogCallBack);
        }
        return carListDialog;
    }


    /**
     * 两个按钮的提示框
     */
    public static void showAlertDialogChoose(Context context, String title, String content,
                                             String leftBtnText, String rightBtnText, boolean rightUser, boolean cancelable,
                                             boolean canceledOnTouchOutside, final DialogInterface.OnClickListener listener) {
        final Dialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCancelable(cancelable);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cst_widget_dialog);
        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        ok.setVisibility(View.GONE);
        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        TextView contentTv = (TextView) window.findViewById(R.id.alert_dialog_content_tv);
        Button leftBtn = (Button) window.findViewById(R.id.alert_dialog_left_btn);
        Button rightBtn = (Button) window.findViewById(R.id.alert_dialog_right_btn);

        leftBtn.setText(leftBtnText);
        rightBtn.setText(rightBtnText);
        titleTv.setText(title);
        contentTv.setText(content);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(dlg, BUTTON1);
                } else {
                    dlg.dismiss();
                }
            }
        });
        if (rightUser) {
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(dlg, BUTTON2);
                    } else {
                        dlg.dismiss();
                    }
                }
            });
        } else {
            rightBtn.setTextColor(context.getResources().getColor(R.color.cst_edit_input_hint));
        }
    }

    /**
     * 两个输入框的提示框
     */
    public static void showEditAlertDialogChoose(Context context, String title, String leftBtnText, String rightBtnText, boolean rightUser, boolean cancelable,
                                                 boolean canceledOnTouchOutside, final DialogCallback callback) {
        final Dialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCancelable(cancelable);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cst_widget_edit_dialog);
        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        ok.setVisibility(View.GONE);
        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        final EditText appIdTv = (EditText) window.findViewById(R.id.alert_dialog_appid_edtv);
        final EditText appSecTv = (EditText) window.findViewById(R.id.alert_dialog_appsec_edtv);
        Button leftBtn = (Button) window.findViewById(R.id.alert_dialog_left_btn);
        Button rightBtn = (Button) window.findViewById(R.id.alert_dialog_right_btn);
        appIdTv.setHint(leftBtnText);
        appSecTv.setHint(rightBtnText);
        titleTv.setText(title);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.OnLeftBtnClick(dlg,appIdTv.getText().toString(), appSecTv.getText().toString());
                } else {
                    dlg.dismiss();
                }
            }
        });
        if (rightUser) {
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        } else {
            rightBtn.setTextColor(context.getResources().getColor(R.color.cst_edit_input_hint));
        }
    }


    /**
     * 一个输入框的提示框
     */
    public static void showOneEditAlertDialogChoose(Context context, String title, String leftBtnText, boolean rightUser, boolean cancelable,
                                                 boolean canceledOnTouchOutside, final DialogCallback callback) {
        final Dialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCancelable(cancelable);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cst_widget_one_edit_dialog);
        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        ok.setVisibility(View.GONE);
        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        TextView titleTip = (TextView) window.findViewById(R.id.alert_tip);
        final EditText appIdTv = (EditText) window.findViewById(R.id.alert_one_dialog_appid_edtv);
        Button leftBtn = (Button) window.findViewById(R.id.alert_dialog_left_btn);
        Button rightBtn = (Button) window.findViewById(R.id.alert_dialog_right_btn);
        appIdTv.setHint(leftBtnText);
        titleTv.setText(title);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.OnOneLeftBtnClick(dlg,appIdTv.getText().toString());
                } else {
                    dlg.dismiss();
                }
            }
        });
        if (rightUser) {
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        } else {
            rightBtn.setTextColor(context.getResources().getColor(R.color.cst_edit_input_hint));
        }
    }

    /**
     * 一个输入框的提示框
     */
    public static void showOneEditAlertDialogChoose(Context context, String title, String leftBtnText, String Tip, boolean rightUser, boolean cancelable,
                                                    boolean canceledOnTouchOutside, final DialogCallback callback) {
        final Dialog dlg = new AlertDialog.Builder(context).create();
        dlg.setCancelable(cancelable);
        dlg.setCanceledOnTouchOutside(canceledOnTouchOutside);
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cst_widget_one_edit_dialog);
        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        TextView titleTv = (TextView) window.findViewById(R.id.alert_dialog_title_tv);
        TextView titleTip = (TextView) window.findViewById(R.id.alert_tip);
        final EditText appIdTv = (EditText) window.findViewById(R.id.alert_one_dialog_appid_edtv);
        Button leftBtn = (Button) window.findViewById(R.id.alert_dialog_left_btn);
        Button rightBtn = (Button) window.findViewById(R.id.alert_dialog_right_btn);
        appIdTv.setHint(leftBtnText);
        titleTv.setText(title);
        titleTip.setText(Tip);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.OnOneLeftBtnClick(dlg,appIdTv.getText().toString());
                } else {
                    dlg.dismiss();
                }
            }
        });
        if (rightUser) {
            rightBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dlg.dismiss();
                }
            });
        } else {
            rightBtn.setTextColor(context.getResources().getColor(R.color.cst_edit_input_hint));
        }
    }

    /**
     *
     */

    public static void showAuthInfoAlertDialog(Context context,String phoneNum,String plateNum,String userID,String carID)
    {
        final Dialog dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window window = dlg.getWindow();
        window.setContentView(R.layout.cst_widget_auth_info);
        Button ok = (Button) window.findViewById(R.id.alert_dialog_confirm_btn);
        TextView phoneNumTv = (TextView) window.findViewById(R.id.alert_dialog_phone_num);
        TextView plateNumTv = (TextView) window.findViewById(R.id.alert_dialog_plate_num);
        TextView userIDTv = (TextView) window.findViewById(R.id.alert_dialog_user_id);
        TextView carIDTv = (TextView) window.findViewById(R.id.alert_dialog_car_id);
        phoneNumTv.setVisibility(View.GONE);
        phoneNumTv.setText(phoneNum);
        plateNumTv.setText(plateNum);
        userIDTv.setText(userID);
        carIDTv.setText(carID);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });

    }
}
