package platform.cston.explain.utils;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import platform.cston.httplib.Cston;


/**
 * Created by zhou-pc on 2016/4/6.
 */
public class CstPlatformUtils {
    private static Toast mToast;

    public static int dip2px(Context context,float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    /**
     * 显示视图
     */
    public static void visible(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 隐藏视图
     */
    public static void invisible(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 消失视图
     */
    public static void gone(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }


    /**
     * 显示长短线
     *
     * @param shortLine    短线视图
     * @param longLine     长线视图
     * @param position     当前项位置
     * @param longPosition 需要显示长线的位置
     */
    public static void showShortOrLongLine(View shortLine, View longLine, int position,
                                           int longPosition) {
        if (shortLine != null && longLine != null) {
            if (position == longPosition) {
                shortLine.setVisibility(View.GONE);
                longLine.setVisibility(View.VISIBLE);
            } else {
                shortLine.setVisibility(View.VISIBLE);
                longLine.setVisibility(View.GONE);
            }
        }
    }


    public static void show(Context context, String msg) {
        showToast(context, msg, false);
    }

    public static void showToast(Context context, String msg, boolean isLong) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
            mToast.setDuration(isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    /**
     * 隐藏软键盘，无需输入框对象，调用系统方法隐藏软键盘
     */
    public static void hideSoftInput(Activity activity) {
        if (activity != null) {
            final InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                View focusView = activity.getCurrentFocus();
                if (focusView != null) {
                    IBinder windowToken = focusView.getWindowToken();
                    if (windowToken != null) {
                        imm.hideSoftInputFromWindow(windowToken, 0);
                    }
                }
            }
        }
    }



}
