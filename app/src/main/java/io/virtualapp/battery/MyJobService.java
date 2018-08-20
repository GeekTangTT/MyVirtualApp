package io.virtualapp.battery;

import android.app.job.JobParameters;
import android.app.job.JobService;

/**
 * Created by T on 2018/8/16.
 */

public class MyJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
