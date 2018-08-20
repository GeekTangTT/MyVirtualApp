package io.virtualapp.battery;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.PersistableBundle;

import java.util.List;

/**
 * Created by T on 2018/8/16.
 */

public class JobManager {
    //把一些不是实时的紧急的任务放到合适的事件去批量处理
    //避免频繁的唤醒硬件模块
    //避免在不合适的时候执行一些耗电的任务
    static JobManager instance;
    private JobScheduler jobScheduler;
    private Context context;

    public static JobManager getInstance(){
        if (null==instance){
            instance=new JobManager();
        }
        return instance;
    }

    public void init(Context context){
        this.context=context.getApplicationContext();
        jobScheduler= (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
    }

    //添加一个任务
    public void addJob(String jobString){
        if (null==jobScheduler){
            return;
        }
        JobInfo pendingJob=null;
        //整合多个job
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            //查找id是0的job
            pendingJob=jobScheduler.getPendingJob(0);
        }else {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                List<JobInfo> allPendingJobs=jobScheduler.getAllPendingJobs();
                for (JobInfo info:allPendingJobs){
                    if (info.getId()==0){
                        pendingJob=info;
                        break;
                    }
                }
            }
        }

        //找到待执行的job
        if (null!=pendingJob){
            //多个信息拼到一起，上传
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                //数据与Intent一样
                PersistableBundle extras=pendingJob.getExtras();
                //获得上一次设置的数据
                String data=extras.getString("DATA");
                //每条数据用"#"隔开
                jobString=data+"#"+jobString;
                //取消挂着的作业
                jobScheduler.cancel(0);
            }
        }

        //jobid : 0   21版本
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            PersistableBundle extras=new PersistableBundle();
            extras.putString("DATA",jobString);
            //创建一个job
            JobInfo jobInfo=new JobInfo.Builder(0,new ComponentName(context,MyJobService.class))
            //只在充电的时候
            .setRequiresCharging(true)
            //不是蜂窝网络
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
            .setExtras(extras).build();

            //提交任务
            jobScheduler.schedule(jobInfo);
        }


    }

}
