package com.jam00.www.util;

import android.app.Activity;

import com.jam00.www.activity.HomeActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijingtong20 on 2017/7/25.
 */

public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();
    public static void addActivity(Activity activity) {
        String tmpname = activity.getLocalClassName();
        Boolean canAdd = true;
        for (Activity tmpActivity : activities) {
            if (tmpname.equals(tmpActivity.getLocalClassName())) {
                canAdd = false;
            }
        }
        if(canAdd){
            activities.add(activity);
        }
    }
    public static void removeActivity(Activity activity) {
        activity.finish();
        activities.remove(activity);
    }
    //调用此方法退出应用
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
