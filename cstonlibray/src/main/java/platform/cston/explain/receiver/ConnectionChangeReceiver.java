package platform.cston.explain.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by zhou-pc on 2016/4/12.
 */
public class ConnectionChangeReceiver extends BroadcastReceiver {

    private OnNetChangeListener mListen;

    public ConnectionChangeReceiver(OnNetChangeListener listen) {
        mListen = listen;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
            mListen.OnNetChangeResult(false);
        } else {
            mListen.OnNetChangeResult(true);
        }
    }
}