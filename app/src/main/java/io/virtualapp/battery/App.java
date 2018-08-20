package io.virtualapp.battery;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.text.TextUtils;

import java.util.List;

import io.virtualapp.BuildConfig;
import io.virtualapp.battery.location.LocationService;

/**
 * Created by T on 2018/8/16.
 */

public class App extends Application{
    private Intent location;
    private static App application;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!TextUtils.equals(BuildConfig.APPLICATION_ID+":location", getProcessName(Process
                .myPid()))){
            application=this;
            location=new Intent(this,LocationService.class);
            startService(location);
        }
    }

    String getProcessName(int pid) {
        ActivityManager am= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps=am.getRunningAppProcesses();
        if (runningApps==null){
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo:runningApps){
            if (processInfo.pid==pid){
                return processInfo.processName;
            }
        }
        return null;
    }
}
