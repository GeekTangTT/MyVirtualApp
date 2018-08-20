package io.virtualapp.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by T on 2018/8/16.
 */

public class PowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if (TextUtils.equals(action,Intent.ACTION_POWER_CONNECTED)){
            //正在充电
            Log.d("PowerReceiver", "onReceive: 正在充电");
        }else if(TextUtils.equals(action,Intent.ACTION_POWER_DISCONNECTED)){
            //不在充电
            Log.d("PowerReceiver", "onReceive: 不在充电");
        }
    }
}
