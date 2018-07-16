package cn.appssec.downloadmanager;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.taobao.android.dexposed.DexposedBridge;
import com.taobao.android.dexposed.XC_MethodReplacement;
import com.taobao.android.dexposed.XposedHelpers;

import cn.appssec.downloadmanager.AidlRequest;
import cn.appssec.downloadmanager.IDownloadService;


public class JavaHookHelper {

    private static final String TAG = "JavaHookHelper";
    private static JavaHookHelper sJavaHookHelper;

    private JavaHookHelper() {
    }

    public static JavaHookHelper getInstance() {
        if (sJavaHookHelper != null) {
            return sJavaHookHelper;
        }
        return new JavaHookHelper();
    }

    private IDownloadService mIDownloadService;

    public void hook() {
//        Intent service = new Intent();
//        service.setComponent(new ComponentName("cn.appssec.downloadmanager", "cn.appssec.downloadmanager.DownloadService"));
//        service.putExtra("_VA_|_user_id_", VUserHandle.getUserId(0));
//        VirtualCore.get().getContext().bindService(service, mServiceConnection, Context.BIND_AUTO_CREATE);

        Intent intent = new Intent();
        intent.setPackage("cn.appssec.downloadmanager");
        intent.setAction("cn.appssec.downloadmanager.DOWNLOAD");
        VActivityManager.get().bindService(VirtualCore.get().getContext(), intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(TAG, " bind service");
        DexposedBridge.findAndHookMethod(DownloadManager.class, "enqueue", DownloadManager.Request.class, new XC_MethodReplacement() {

            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "replaceHookedMethod enqueue----->" + param.args[0]);
//                IIntentReceiver
                DownloadManager.Request request = (DownloadManager.Request) param.args[0];
                Object objUri = XposedHelpers.getObjectField(request, "mUri");
                String uri = objUri.toString();
//                Object objDestination = XposedHelpers.getObjectField(request, "mDestinationUri");
//                String destination = objDestination.toString();
//                Object objMimeType = XposedHelpers.getObjectField(request, "mMimeType");
//                String mimeType = objMimeType.toString();
//                Intent intent = new Intent();
//                intent.setPackage("cn.appssec.downloadmanager");
//                intent.setAction("cn.appssec.downloadmanager.DOWNLOAD");
//                intent.putExtra("uri", uri);
//                intent.putExtra("destinationUri", destination);
//                intent.putExtra("mime", mimeType);
//                VActivityManager.get().startService(null, intent, null, 0);
                long id = 0L;
                if (mIDownloadService == null) {
                    Log.d(TAG, " mIDownloadService is null");
                } else {
//                    id = mIDownloadService.enqueue(new AidlRequest(uri, destination, mimeType));
                    id = mIDownloadService.enqueue(new AidlRequest(uri, null, null));
                    Log.d("Q_M", "replaceHookedMethod ----->" + objUri.toString());
                    Log.d(TAG, " enqueue id : " + id);
                }
                return id;
            }
        });

//        DexposedBridge.findAndHookMethod(DownloadManager.class, "getDownloadUri", long.class, new XC_MethodReplacement() {
//
//            @Override
//            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
//                Log.e(TAG, "replaceHookedMethod getDownloadUri ----->" + param.args[0]);
////                IIntentReceiver
//                long id = (long) param.args[0];
//                Log.d(TAG, " id = " + id);
//                Uri uri = null;
//                if (mIDownloadService == null) {
//                    Log.d(TAG, " mIDownloadService is null");
//                } else {
//                    uri = mIDownloadService.getDownloadUri(id);
//                    Log.d(TAG, " uri : " + uri);
//                }
//                return uri;
//            }
//        });

        DexposedBridge.findAndHookMethod(DownloadManager.class, "getUriForDownloadedFile", long.class, new XC_MethodReplacement() {

            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "replaceHookedMethod getUriForDownloadedFile----->" + param.args[0]);
//                IIntentReceiver
                long id = (long) param.args[0];
                Log.d(TAG, " id = " + id);
                Uri uri = null;
                if (mIDownloadService == null) {
                    Log.d(TAG, " mIDownloadService is null");
                } else {
                    uri = mIDownloadService.getUriForDownloadedFile(id);
                    Log.d(TAG, " uri : " + uri);
                }
                return uri;
            }
        });

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, " onServiceConnected");
            mIDownloadService = IDownloadService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, " onServiceDisconnected");
            VirtualCore.get().getContext().unbindService(mServiceConnection);
        }
    };

}
