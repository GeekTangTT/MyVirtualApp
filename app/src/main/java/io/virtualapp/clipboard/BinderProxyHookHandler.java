package io.virtualapp.clipboard;


import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * Created by T on 18/5/23.
 */

public class BinderProxyHookHandler implements InvocationHandler {
    private static final String TAG="BinderProxyHookHandler";
    //这是一个BinderProxy对象
    //只有当Service和我们在同一个进程的时候才是Binder本地对象。
    IBinder base;
    //stub存根
    Class<?> stub;
    Class<?> iinterface;

    public BinderProxyHookHandler(IBinder base) throws Exception {
        this.base=base;
        Log.d(TAG, "BinderProxyHookHandler: 被调用");
        try {
            //获得服务的stub，这是aidl内容
            this.stub= Class.forName("android.content.IClipboard$Stub");
            //获得iclipboard的class

            this.iinterface= Class.forName("android.content.IClipboard");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
    //invoke调用引起
    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        //动态修改自定义的IBinder对象
        if ("queryLocalInterface".equals(method.getName())){
            //这是一个hook点，重新被定义了。
            //返回被hook掉的Service接口
            return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                    new Class[]{IBinder.class , IInterface.class,this.iinterface},
                    new BinderHookClip(base,stub));
        }
        //其它的操作跟原来一样
        return method.invoke(base,objects);
    }

}
