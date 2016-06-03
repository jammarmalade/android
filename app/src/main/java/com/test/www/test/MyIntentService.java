package com.test.www.test;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by weijingtong20 on 2016/6/3.
 */
public class MyIntentService extends IntentService {
    public static final String TAG = "MyIntentService";
    public MyIntentService() {
        super("MyIntentService"); // 调用父类的有参构造函数
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        // 打印当前线程的id
        Log.d(TAG, "Thread id is " + Thread.currentThread().getId());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy executed");
    }
}
