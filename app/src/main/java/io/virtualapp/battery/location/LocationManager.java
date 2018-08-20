package io.virtualapp.battery.location;


import android.content.Context;
import android.util.Log;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import io.virtualapp.battery.JobManager;

/**
 * Created by T on 2018/8/16.
 */

public class LocationManager {
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    private static LocationManager instance;
    private Context mContext;

    public LocationManager() {
    }

    public static LocationManager getInstance(){
        if (null==instance){
            instance=new LocationManager();
        }
        return instance;
    }

    //声明定位回调监听器
    public AMapLocationListener aMapLocationListener= aMapLocation -> {
        if (aMapLocation!=null){
            if (aMapLocation.getErrorCode()==0){
                //获得json
                String location=aMapLocation.toStr();
                //启动IntentService上传坐标数据
                //如果不是实时需要是话，延迟执行上传
                // UploadService.UploadLocation(applicationContext, location);
                JobManager.getInstance().addJob(location);
            }else {
                //定位失败，可通过WrrCode错误码信息来确定失败的原因，errinfo是错误信息，详见错误码表。
                Log.e ("AmapError", "location Error,ErrCode:"
                +aMapLocation.getErrorCode()+",errInfo:"
                +aMapLocation.getErrorInfo());
            }
        }
    };

    public void startLocation(Context context){
        if (null!=mLocationClient){
            mLocationClient.startLocation();
            return;
        }
        mContext=context.getApplicationContext();
        //初始化定位
        mLocationClient=new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(aMapLocationListener);
        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption=null;
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(5000);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public void stopLocation() {
        if (null != mLocationClient) {
            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        }
    }

    public void destoryLocation() {
        if (null != mLocationClient) {
            mLocationClient.unRegisterLocationListener(aMapLocationListener);
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mLocationClient = null;
        }
    }


}
