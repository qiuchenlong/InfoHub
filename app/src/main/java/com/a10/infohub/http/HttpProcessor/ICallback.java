package com.a10.infohub.http.HttpProcessor;

/**
 * Created by qiuchenlong on 2017/8/8.
 */

public interface ICallback {

    void onSuccess(String result);
    void onFailure(String error);

}
