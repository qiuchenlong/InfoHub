package com.a10.infohub;

import android.app.Application;

import com.a10.infohub.utils.HookAmsUtil;

/**
 * Created by qiuchenlong on 2017/8/6.
 */

public class MyApplication extends Application {
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
    }
}
