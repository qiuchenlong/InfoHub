package com.a10.infohub.base;


import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.a10.infohub.ProxyActivity;
import com.a10.infohub.http.HttpProcessor.HttpHelper;
import com.a10.infohub.http.HttpProcessor.OkHttpProcessor;
import com.a10.infohub.http.HttpProcessor.VolleyProcessor;
import com.a10.infohub.utils.HookAmsUtil;

/**
 * 处理解决mutiDex的问题
 *
 * Created by qiuchenlong on 2017/6/4.
 */

public class BaseApplication extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //支持多dex的apk安装
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HookAmsUtil amsUtil = new HookAmsUtil(ProxyActivity.class, this);
        try {
            amsUtil.hookAms();
            amsUtil.hookSystemHandler();
        } catch (Exception e){
            e.printStackTrace();
        }


        // 网络框架选择
//        HttpHelper.init(new VolleyProcessor(this));
        HttpHelper.init(new OkHttpProcessor());
    }

}
