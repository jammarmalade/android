package com.test.www.test;

/**
 * Created by weijingtong20 on 2016/6/23.
 */
import android.app.Application;
/**
 * Application类，提供全局上下文对象
 * @author Rabbit_Lee
 *
 */
public class MyApplication extends Application {

    public static String TAG;
    public static MyApplication myApplication;

    public static MyApplication newInstance() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TAG = this.getClass().getSimpleName();
        myApplication = this;

    }
}
