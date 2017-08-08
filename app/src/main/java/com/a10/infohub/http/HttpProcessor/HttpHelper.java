package com.a10.infohub.http.HttpProcessor;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import static com.a10.infohub.http.HttpProcessor.HttpHelper.appendParams;

/**
 * Create on 2017年08月08日 16:42
 *
 * @author: 邱晨龙
 * @email: Cyndi@10.com
 * @QQ: 601976246
 * @phone: 13950209512
 * <p>
 * Copyright(c) __10.com__. All rights reserved.
 */

public class HttpHelper implements IHttpProcessor {

    private static IHttpProcessor mIHttpProcessor = null;
    private Map<String, Object> mParams;
    private static HttpHelper _instance;

    private HttpHelper(){
        mParams = new HashMap<>();
    }

    public static HttpHelper obtain(){
        synchronized (HttpHelper.class) {
            if(_instance == null) {
                _instance = new HttpHelper();
            }
        }
        return _instance;
    }

    public static void init(IHttpProcessor httpProcessor){
        mIHttpProcessor = httpProcessor;
    }

    @Override
    public void post(String url, Map<String, Object> params, ICallback callback) {
        final String finalUrl = appendParams(url, params);
        mIHttpProcessor.post(finalUrl, params, callback);
    }

    @Override
    public void get(String url, Map<String, Object> params, ICallback callback) {
        final String finalUrl = appendParams(url, params);
        mIHttpProcessor.get(finalUrl, params, callback);
    }

    public static String appendParams(String url, Map<String, Object> params) {
        if(params == null || params.isEmpty()) {
            return url;
        }
        StringBuffer urlBuilder = new StringBuffer();

        if(!url.isEmpty()){
            urlBuilder.append(url);

            if (!urlBuilder.toString().endsWith("?")) {
                urlBuilder.append("&");
            }
        }


        for(Map.Entry<String, Object> entry : params.entrySet()) {
            urlBuilder.append(entry.getKey()).append("=").append(encode(entry.getValue().toString()));
        }

        return urlBuilder.toString();
    }


    // URL不允许有空格等字符，如果参数值有空格，需要此方法转换
    private static String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
            Log.e("参数转码异常", e.toString());
            throw new RuntimeException(e);
        }
    }

}
