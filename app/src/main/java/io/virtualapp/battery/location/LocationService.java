package io.virtualapp.battery.location;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;

import io.virtualapp.battery.JobManager;

/**
 * Created by T on 2018/8/16.
 */

public class LocationService extends Service{
    private PowerManager.WakeLock locationLock;
    private Intent alarmIntent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        JobManager.getInstance().init(this);


    }
}
