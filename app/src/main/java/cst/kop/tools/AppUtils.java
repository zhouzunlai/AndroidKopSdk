package cst.kop.tools;

import android.content.Context;
import android.content.SharedPreferences;

import platform.cston.httplib.Cston;

/**
 * Created by zhou-pc on 2016/9/27.
 */
public class AppUtils {

    public static void setAppinfo(Context context, String appid, String appkey) {
        try {
            SharedPreferences settings = context.getSharedPreferences("platform_appinfo_sharepreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("KEY_APP_ID", appid);
            editor.putString("KEY_APP_KEY", appkey);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAppinfo(Context context) {
        String result1;
        String result2;
        try {
            SharedPreferences settings = context.getSharedPreferences("platform_appinfo_sharepreference", Context.MODE_PRIVATE);
            result1 = settings.getString("KEY_APP_ID", null);
            result2 = settings.getString("KEY_APP_KEY", null);
            if (null != result1 && null != result2) {
                Cston.Auth.setApp_Info(result1, result2);
            } else {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAuthFlag(Context context) {
        try {
            SharedPreferences settings = context.getSharedPreferences("platform_appinfo_sharepreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("KEY_APP_AUTH", "CANCEL");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clearnAuthFlag(Context context) {
        try {
            SharedPreferences settings = context.getSharedPreferences("platform_appinfo_sharepreference", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getAuthFlag(Context context) {
        String result1;
        try {
            SharedPreferences settings = context.getSharedPreferences("platform_appinfo_sharepreference", Context.MODE_PRIVATE);
            result1 = settings.getString("KEY_APP_AUTH", null);
            return result1;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
