package com.a10.infohub.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by qiuchenlong on 2017/8/6.
 */

public class HookAmsUtil {
    private Class<?> proxyActivity;
    private Context context;
    private Object activityThreadValue; // 系统程序入口ActivityThread对象

    public HookAmsUtil(Class<?> proxyActivity, Context context){
        this.proxyActivity = proxyActivity;
        this.context = context;
    }

    public void hookAms() throws Exception{
        Log.i("INFO", "start hook");
        Class<?> forName = Class.forName("android.app.ActivityManagerNative");
        Field defaultField = forName.getDeclaredField("gDefault");
        defaultField.setAccessible(true);
        // gDefault变量值
        Object defaultValue = defaultField.get(null);
        // 反射SingleTon
        Class<?> forName2 = Class.forName("android.util.Singleton");
        Field instanceFiled = forName2.getDeclaredField("mInstance");
        instanceFiled.setAccessible(true);
        // 系统的iActivityManager对象
        Object iActivityManagerObject = instanceFiled.get(defaultValue);
        // 钩子
        Class<?> iActivityManagerIntercept = Class.forName("android.app.IActivityManager");
        AmsInvocationHandler handler = new AmsInvocationHandler(iActivityManagerObject);
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class<?>[]{iActivityManagerIntercept}, handler);

        instanceFiled.set(defaultValue, proxy);
    }

    class AmsInvocationHandler implements InvocationHandler{
        private Object iActivityManagerObject;

        public AmsInvocationHandler(Object iActivityManagerObject){
            this.iActivityManagerObject = iActivityManagerObject;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Log.i("INFO", "methodName:" + method.getName());
            if("startActivity".contains(method.getName())){
                // 偷天换日
                Intent intent = null;
                int index = 0;
                for (int i=0; i<args.length; i++){
                    if(args[i] instanceof Intent){
                        // 说明找到了startActivity方法的Intent参数
                        intent = (Intent) args[i];
                        index = i;
                        break;
                    }
                }
                Intent proxyIntent = new Intent();
                ComponentName componentName = new ComponentName(context, proxyActivity);
                proxyIntent.setComponent(componentName);
                proxyIntent.putExtra("oldIntent", intent);
                args[index] = proxyIntent;
                return method.invoke(iActivityManagerObject, args);
            }
            return method.invoke(iActivityManagerObject, args);

        }
    }



    public void hookSystemHandler(){
        try {
            Class<?> forName = Class.forName("android.app.ActivityThread");
            Field currentActivityThreadFiled = forName.getDeclaredField("sCurrentActivityThread");
            currentActivityThreadFiled.setAccessible(true);
            activityThreadValue = currentActivityThreadFiled.get(null);
            Field handlerField = forName.getDeclaredField("mH");
            handlerField.setAccessible(true);
            // mH的变量值
            Handler handlerObject = (Handler) handlerField.get(activityThreadValue);
            Field callbackField = Handler.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            callbackField.set(handlerObject, new ActivityThreadHandlerCallback(handlerObject));
        }catch (Exception e){

        }
    }

    class ActivityThreadHandlerCallback implements Handler.Callback{
        Handler handler;

        public ActivityThreadHandlerCallback(Handler handler){
            super();
            this.handler = handler;
        }

        @Override
        public boolean handleMessage(Message msg) {
            Log.i("INFO", "message callback");
            //替换回来之前的Intent
            if(msg.what == 100){
                Log.i("INFO", "launchActivity");
                handlerLaunchActivity(msg);
            }
            handler.handleMessage(msg);
            return true;
        }

        private void handlerLaunchActivity(Message msg){
            Object obj = msg.obj; // ActivityClientRecord
            try {
                // 继续反射
                Field intentField = obj.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent proxyIntent = (Intent) intentField.get(obj); // proxyIntent
                Intent realIntent = proxyIntent.getParcelableExtra("oldIntent");
                if(realIntent != null){
                    proxyIntent.setComponent(realIntent.getComponent());
                }
            }catch (Exception e) {

            }
        }
    }

}
