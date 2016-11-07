package cst.kop.tools;

import android.app.Dialog;

/**
 * Created by zhou-pc on 2016/9/13.
 */
public interface OneDialogCallback {
    void OnPositiveBtnClick(String errorCode);

    void OnNegativeBtnClick(Dialog dialog);
}
