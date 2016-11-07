package cst.kop.tools;

import android.app.Dialog;

/**
 * Created by zhou-pc on 2016/9/13.
 */
public interface DialogCallback {
    void OnLeftBtnClick(Dialog dlg, String appid, String appsec);

    void OnOneLeftBtnClick(Dialog dlg, String phone);
}
