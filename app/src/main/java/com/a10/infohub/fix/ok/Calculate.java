package com.a10.infohub.fix.ok;

import com.a10.infohub.fix.Replace;

/**
 * Created by qiuchenlong on 2017/6/24.
 * 服务端进行编程
 */

public class Calculate {

    @Replace(clazz = "com.a10.infohub.fix.Calculate", method = "caculutor")
    public int caculutor() {
        // 修复
        int i=1;
        int j=100;
        return j/i;
    }

}
