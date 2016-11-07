
package platform.cston.explain.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;

import cston.cstonlibray.R;


public class ActionSheetDialog extends Dialog {

    public ActionSheetDialog(Context context) {
        this(context, R.style.cst_platform_ActionSheetDialog);
        init(context);
    }

    public ActionSheetDialog(Context context, int theme) {
        super(context, theme == 0 ? R.style.cst_platform_ActionSheetDialog : theme);
        init(context);
    }

    private void init(Context context) {
        final Window window = getWindow();
        // 设置dialog显示的位置
        window.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }
}
