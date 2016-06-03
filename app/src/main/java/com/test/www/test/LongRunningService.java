package com.test.www.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

/**
 * Created by weijingtong20 on 2016/6/3.
 */
public class LongRunningService extends Service {
    public static final String TAG = "LongRunningService";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "executed at " + new Date().toString());
            }
        }).start();
        //创建定时任务管理器
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 60 * 60 * 1000; // 这是一小时的毫秒数
        //触发时间
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;

        Intent i = new Intent(this, AlarmReceiver.class);
        //使用 PendingIntent 指定处理定时任务的广播接收器为 AlarmReceiver
        // getBroadcast(Context context, int requestCode, Intent intent, int flags)
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }
}
