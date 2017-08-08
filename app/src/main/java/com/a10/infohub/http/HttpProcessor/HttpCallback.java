package com.a10.infohub.http.HttpProcessor;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Created by qiuchenlong on 2017/8/8.
 */

public abstract class HttpCallback<Result> implements ICallback {
    @Override
    public void onSuccess(String result) {
        Gson gson = new Gson();
        Class<?> clz = analysisClassInfo(this);
        Result objResult = (Result) gson.fromJson(result, clz);
        onSuccess(objResult);
    }

    public abstract void onSuccess(Result result);

    public static Class<?> analysisClassInfo(Object object){
        Type genType = object.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType)genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }

//    @Override
//    public void onFailure(String error) {
//
//    }
}
