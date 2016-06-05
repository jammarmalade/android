package com.test.www.test;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/6/5.
 */
public class MyApplication extends Application {
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        //得到一个应用程序级别的 Context
        context = getApplicationContext();
    }
    public static Context getContext() {
        return context;
    }
}

