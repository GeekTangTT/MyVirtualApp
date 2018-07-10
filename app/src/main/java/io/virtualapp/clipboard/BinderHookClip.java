package io.virtualapp.clipboard;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


/**
 * Created by T on 18/5/23.
 */

public class BinderHookClip implements InvocationHandler {
    final SecClipData secClipData =new SecClipData();
    private static final String TAG="BinderHookClip";
    //原始的Service对象（IIterface）
    Object base;
    public BinderHookClip(IBinder base , Class<?> stubClass){
        Log.d(TAG, "BinderHookClip: 被调用");
        try {
            //获取stub的asInterface静态方法
            Method asInterfaceMethod=stubClass.getDeclaredMethod("asInterface",IBinder.class);
            //IClipboard.Stub.asInterface(base);
            //获得Android.content.IClipboard实例 这是系统默认的实例
            this.base=asInterfaceMethod.invoke(null,base);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {

        Object object=objects[0];
        String str=object.toString();
        Log.d(TAG, "剪切板原数据："+str);

        if ("hasPrimaryClip".equals(method.getName())){
            Log.d(TAG, "hasPrimaryClip数据："+str);
            return true;
        }

        if ("setPrimaryClip".equals(method.getName())){
            Log.d(TAG, "进入setPrimary1");
            String s="";

            if (str.startsWith("ClipData { text/plain")){
                Log.d(TAG, "进入setPrimary2");
                String[] ss=str.split("T:",2);
                if (ss.length != 1){
                    if (ss[1].endsWith("} }")){
                        s=ss[1].substring(0,ss[1].length()-3);
                    }else {
                        s="";
                    }
                }else {
                    s="";
                }
            }else if (str.startsWith("ClipData { text/html")){
                String[] ss=str.split(">");
                String[] sss=ss[1].split("<");
                s=sss[0];
            }else {
                s="";
            }
            putFile(s);
            Log.d(TAG, "setPrimaryClip: "+s);
            return ClipData.newPlainText(null,s);
        }

        //Log.d(TAG, "类："+o.toString()+"；方法:"+method.getName().toString());
//        for (int i=0;i<objects.length;i++){
//            Log.d(TAG, "方法:"+method.getName().toString()+"参数："+objects[i].toString());
//        }


        //Log.d(TAG, "invoke: ----------------");
//        if ("getPrimaryClipDescription".equals(method.getName())){
//            Log.d(TAG, "getPrimaryClipDescription: "+objects.toString());
//            //return ClipDescription.compareMimeTypes();
//        }
//        if ("getText".equals(method.getName())){
//            Log.d(TAG, "getText: "+o+"----"+method+"----"+objects.toString());
//            return ClipData.newPlainText(null,objects.toString());
//        }

        //把剪切板的内容替换为"你已经被hook了"
        if ("getPrimaryClip".equals(method.getName())){
            //Log.d(TAG, "getPrimaryClip: "+objects.toString());
            //return ClipData.newPlainText(null,"你已经被hook了");
            //return ClipData.newPlainText(null,"aaaaaa");
            getClipData();
            Thread.sleep(100);
            if (secClipData ==null){
                return ClipData.newPlainText(null, null);
            }else {
                String s= secClipData.getClipData().toString();
                Log.d(TAG, "getPrimaryClip: "+s);
                return ClipData.newPlainText(null, s);
            }
        }

//        //欺骗系统，剪切板上一直有内容
//        if ("hasPrimaryClip".equals(method.getName())){
//            Log.d(TAG, "hasPrimaryClip: "+objects.toString());
//            return  true;
//        }

        return method.invoke(base,objects);
    }
    private void putFile(final String s) {
        new Thread(() -> {
            SecClipData secClipData =new SecClipData();
            secClipData.setClipData(s);
            File sdcardDir=Environment.getExternalStorageDirectory();
            String path=sdcardDir.getPath();
            File ClipPath=new File(path+"/cd.tx");
            if (!sdcardDir.exists()){
                sdcardDir.mkdirs();
            }
            if (!ClipPath.exists()){
                try {
                    ClipPath.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (ClipPath.exists()){
                ClipPath.delete();
                try {
                    ClipPath.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ObjectOutputStream objectOutputStream=null;
            try {
                objectOutputStream=new ObjectOutputStream(new FileOutputStream(ClipPath));
                objectOutputStream.writeObject(secClipData);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (objectOutputStream !=null){
                        objectOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void getClipData() {
        new Thread(() -> {
            File file=new File(Environment.getExternalStorageDirectory().getPath()+"/cd.tx");
            if (file.exists()){
                ObjectInputStream objectInputStream=null;
                try {
                    objectInputStream=new ObjectInputStream(new FileInputStream(file));
                    SecClipData clipData = (SecClipData) objectInputStream.readObject();
                    secClipData.setClipData(clipData.getClipData());

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if (objectInputStream != null){
                            objectInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
