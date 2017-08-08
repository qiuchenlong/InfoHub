package com.a10.infohub.http.HttpProcessor;

import java.util.Map;

/**
 * Create on 2017年08月08日 16:33
 *
 * @author: 邱晨龙
 * @email: Cyndi@10.com
 * @QQ: 601976246
 * @phone: 13950209512
 * <p>
 * Copyright(c) __10.com__. All rights reserved.
 */

public interface IHttpProcessor {

    // 网络访问：Post，Get，Del，Update，Put
    void post(String url, Map<String, Object> params, ICallback callback);

    void get(String url, Map<String, Object> params, ICallback callback);

}
