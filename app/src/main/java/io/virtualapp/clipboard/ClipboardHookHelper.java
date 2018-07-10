package io.virtualapp.clipboard;

import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Created by T on 18/5/22.
 */

public class ClipboardHookHelper {
    static final String TAG="ClipboardHookHelper";

    public static void hookClipboardService() throws Exception {
        Log.d(TAG, "hookClipboardService: 被调用");
        final String CLIPBOARD_SERVICE="clipboard";

        //获得ServiceManager的class
        Class<?> serviceManager= Class.forName("android.os.ServiceManager");
        //找到getService静态方法
        Method getService=serviceManager.getDeclaredMethod("getService",String.class);
        //这是binder代理对象，原始对象在另一个进程中    ？有文章说这是原始binder对象
        IBinder rawBinder= (IBinder) getService.invoke(null,CLIPBOARD_SERVICE);

        //ClipBoardStub clipBoardStub=new ClipBoardStub();

        //Hook掉这个binder代理对象的querylocalinterface方法
        //返回一个IInterface对象，hook掉我们感兴趣的方法就可以
        IBinder hookedBinder= (IBinder) Proxy.newProxyInstance(serviceManager.getClassLoader(),
                new Class[]{IBinder.class},
                new BinderProxyHookHandler(rawBinder));//定制自己的IBinder对象
                //new BinderProxyHookHandler(clipBoardStub.inject());

        //把这个hook过的binder代理对象注入到ServiceManager的cache里
        //查询时先找缓冲的binder，直接使用hook修改过的binder了
        Field cacheField=serviceManager.getDeclaredField("sCache");
        cacheField.setAccessible(true);
        Map<String,IBinder> cache= (Map) cacheField.get(null);
        cache.put(CLIPBOARD_SERVICE,hookedBinder);
    }
}
