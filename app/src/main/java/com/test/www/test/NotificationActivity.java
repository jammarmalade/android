package com.test.www.test;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;

/**
 * Created by weijingtong20 on 2016/6/2.
 */
public class NotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);
        //取消导航栏上的通知
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //对应创建时的 id 值
        manager.cancel(1);
    }
}
