package cn.appssec.downloadmanager;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
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
    private Context context;


    private JavaHookHelper() {
    }

    public static JavaHookHelper getInstance() {
        if (sJavaHookHelper != null) {
            return sJavaHookHelper;
        }
        return new JavaHookHelper();
    }

    private IDownloadService mIDownloadService;


    public void hook(Context con) {
        context=con;
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


        DexposedBridge.findAndHookMethod(DownloadManager.class, "query", DownloadManager.Query.class, new XC_MethodReplacement() {
            Cursor cursor;
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "replaceHookedMethod query----->" + param.args[0]);
                DownloadManager.Query query= (DownloadManager.Query) param.args[0];

                Object mIdObj = XposedHelpers.getObjectField(query, "mIds");
                long[] mIds = (long[]) mIdObj;

                ContentResolver resolver = context.getContentResolver();
                Uri uri = Uri.parse("content://cn.appssec.downloadmanager.SecContentProvider/download_info/");

//                List<String> selectionParts = new ArrayList<String>();
//                String[] selectionArgs = null;
//
//                int whereArgsCount = (mIds == null) ? 0 : mIds.length;

                String sb[] = new String[mIds.length];
                for (int i = 0; i < mIds.length; i++) {
                    sb[i] = String.valueOf(mIds[i]);
                }

                //Log.d("Q_M", "JavaHookHelper----->query....." + Arrays.toString(sb));

                cursor = resolver.query(uri, null, null, sb, null);
                //Log.d("Q_M", "JavaHookHelper----->query....." + cursor.getCount());

//                if (cursor != null) {
//                    while (cursor.moveToNext()) {
//
//                        String bytesDownload = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                        String descrition = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION));
//                        String id = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
//                        String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
//                        String mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE));
//                        String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
//                        String status = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
//                        String totalSize = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//
//                        Log.d("Q_M", "bytesDownload:" + bytesDownload);
//                        Log.d("Q_M", "descrition:" + descrition);
//                        Log.d("Q_M", "id:" + id);
//                        Log.d("Q_M", "localUri:" + localUri);
//                        Log.d("Q_M", "mimeType:" + mimeType);
//                        Log.d("Q_M", "title:" + title);
//                        Log.d("Q_M", "status:" + status);
//                        Log.d("Q_M", "totalSize:" + totalSize);
//                    }
//                }
                return cursor;
            }
        });



        DexposedBridge.findAndHookMethod(DownloadManager.class, "getDownloadUri", long.class, new XC_MethodReplacement() {

            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "replaceHookedMethod getDownloadUri ----->" + param.args[0]);
//                IIntentReceiver
                long id = (long) param.args[0];
                Log.d(TAG, " id = " + id);
                Uri uri = null;
                if (mIDownloadService == null) {
                    Log.d(TAG, " mIDownloadService is null");
                } else {
                    uri = mIDownloadService.getDownloadUri(id);
                    Log.d(TAG, " uri : " + uri);
                }
                return uri;
            }
        });

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

        DexposedBridge.findAndHookMethod(DownloadManager.class, "openDownloadedFile", long.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "replaceHookedMethod openDownloadedFile----->" + param.args[0]);
                long id= (long) param.args[0];
                ParcelFileDescriptor parcelFileDescriptor = null;
                if (mIDownloadService==null){
                    Log.d(TAG, " mIDownloadService is null");
                }else {
                    parcelFileDescriptor=mIDownloadService.openDownloadedFile(id);
                    Log.d(TAG, " parcelFileDescriptor:"+parcelFileDescriptor);
                }
                return parcelFileDescriptor;
            }
        });
        DexposedBridge.findAndHookMethod(DownloadManager.class, "getMimeTypeForDownloadedFile", long.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                Log.e(TAG, "replaceHookedMethod getMimeTypeForDownloadedFile----->" + param.args[0]);
                long id= (long) param.args[0];
                //cursor=downloadManager.query(query);
                //cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE));
                String string=null;
                if (mIDownloadService != null){
                    string=mIDownloadService.getMimeTypeForDownloadedFile(id);
                    Log.d(TAG, "Cursor  string:"+string);
                }else {}
                return string;
            }
        });
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "下载服务已连接");
            mIDownloadService = IDownloadService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "下载服务已解绑");
            VirtualCore.get().getContext().unbindService(mServiceConnection);
        }
    };

}
