package com.test.www.test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by weijingtong20 on 2016/6/1.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "received in MyBroadcastReceiver", Toast.LENGTH_SHORT).show();
        //若是有序广播，可以使用  abortBroadcast 将这条广播截断， 后面的广播接收器将无法再接收到这条广播
        abortBroadcast();
    }
}
