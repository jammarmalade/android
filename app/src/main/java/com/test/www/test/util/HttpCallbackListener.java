package com.test.www.test.util;

/**
 * Created by Administrator on 2016/6/10.
 * http 请求的回调接口
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(String msg,Exception e);
}
