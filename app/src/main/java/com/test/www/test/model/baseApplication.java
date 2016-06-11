package com.test.www.test.model;

import android.app.Application;
import android.content.Context;

/**
 * Created by Administrator on 2016/6/11.
 * 自定义 Application
 */
public class BaseApplication extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
    //全局获取 context
    public static Context getContext() {
        return context;
    }
}
