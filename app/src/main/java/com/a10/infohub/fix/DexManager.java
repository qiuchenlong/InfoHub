package com.a10.infohub.fix;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import dalvik.system.DexFile;


/**
 * Created by qiuchenlong on 2017/6/24.
 */

public class DexManager {
    private Context context;
    private String TAG = "qiuchenlong";

    public DexManager(Context context) {
        this.context = context;
    }

    public void loadDex(File dexFilePath) {
        File optFile = new File(context.getCacheDir(), dexFilePath.getName());
        if (optFile.exists()) {
            optFile.delete();
        }
        try {
            Log.d("TAG", dexFilePath.getAbsolutePath() + "," + optFile.getAbsolutePath());
            // 加载dex
//            DexFile dexFile = new DexFile(optFile.getAbsoluteFile());
//            DexFile dexFile = new DexFile (dexFilePath.getAbsolutePath(), optFile.getAbsolutePath(), Context.MODE_PRIVATE);

            DexFile dexFile = DexFile.loadDex(dexFilePath.getAbsolutePath(), optFile.getAbsolutePath(), Context.MODE_PRIVATE);
            // 遍历dex里面的class
            Enumeration<String> entry = dexFile.entries();
            while (entry.hasMoreElements()) {
                // 遍历class
                String className = entry.nextElement();
                // 修复好的realClazz   怎么样找到 出bug的calss
                Class realClazz = dexFile.loadClass(className, context.getClassLoader());
                Log.i(TAG, "找到类   " + className);

                // 修复
                fix(realClazz);

            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void fix(Class realClazz) {
        Method[] methods = realClazz.getDeclaredMethods();
        for (Method method : methods) {
            // 拿到注解
            Replace replace = method.getAnnotation(Replace.class);
            if (replace == null) {
                continue;
            }
            String wrongClazzName = replace.clazz();
            String wrongMethodName = replace.method();

            try {
                Class wrongClass = Class.forName(wrongClazzName);
                Log.d(TAG, wrongClazzName + wrongMethodName);
                // 最终拿到错误的Method对象
                Method wrongMethod = wrongClass.getMethod(wrongMethodName, method.getParameterTypes());
                // 修复
                replace(wrongMethod, method);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private native void replace(Method wrongMethod, Method method);

}
