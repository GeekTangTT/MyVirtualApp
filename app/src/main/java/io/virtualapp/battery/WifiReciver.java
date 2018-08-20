package io.virtualapp.battery;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by T on 2018/8/16.
 */

public class WifiReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Battery.isWifi(context)){
            Log.d("WifiReciver", "onReceive: 当前在使用WiFi");
        }else {
            Log.d("WifiReciver", "onReceive: 当前不在使用WiFi");
        }
    }
}
