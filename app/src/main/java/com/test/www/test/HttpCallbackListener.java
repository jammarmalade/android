package com.test.www.test;

/**
 * Created by Administrator on 2016/6/5.
 * http 请求返回后的回调接口
 */
public interface HttpCallbackListener {
    //当服务器成功响应我们请求的时候调用
    void onFinish(String response);
    //当进行网络操作出现错误的时候调用
    void onError(Exception e);
}
