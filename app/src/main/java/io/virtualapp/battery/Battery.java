package io.virtualapp.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.BatteryManager;

/**
 * Created by T on 2018/8/16.
 */

public class Battery {
    /**
     * 是否正在充电
     * @param context
     * @return
     */
    public static boolean isPlugged(Context context){
        //发送一个包含充电状态的广播，并且是一个持续的广播
        IntentFilter filter=new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent=context.registerReceiver(null,filter);
        //获取充电状态
        int isPlugged=intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
        boolean acPlugged=isPlugged==BatteryManager.BATTERY_PLUGGED_AC;
        boolean usbPlugged=isPlugged==BatteryManager.BATTERY_PLUGGED_USB;
        boolean wifiPlugged=isPlugged==BatteryManager.BATTERY_PLUGGED_WIRELESS;

        return acPlugged||usbPlugged||wifiPlugged;
    }

    /**
     * 是否在使用WiFi
     * @param context
     * @return
     */
    public static boolean isWifi(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取当前网络信息
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if (null!=networkInfo && networkInfo.isConnected() &&
                networkInfo.getType()==ConnectivityManager.TYPE_WIFI){
            return true;
        }
        //7.0以后不能静态注册了，需要动态获取
        //intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        cm.requestNetwork(new NetworkRequest.Builder().build(),
                new ConnectivityManager.NetworkCallback(){
                    @Override
                    public void onAvailable(Network network) {
                        super.onAvailable(network);
                        //网络状态改变
                    }
                });
        return false;
    }
}
