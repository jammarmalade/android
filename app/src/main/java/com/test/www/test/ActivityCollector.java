package com.test.www.test;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijingtong20 on 2016/5/30.
 * 管理所有的 Activity
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    /**
     * 要退出程序时调用此方法，可随时退出程序
     */
    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
