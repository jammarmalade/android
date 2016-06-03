package com.test.www.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by weijingtong20 on 2016/6/3.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //构建出了一个 Intent 对象，然后去启动 LongRunningService 这个服务
        //也就是再次去运行 LongRunningService 这个服务，也就永久运行了
        Intent i = new Intent(context, LongRunningService.class);
        context.startService(i);
    }
}
