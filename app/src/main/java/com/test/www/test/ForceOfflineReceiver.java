package com.test.www.test;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by weijingtong20 on 2016/6/1.
 */
public class ForceOfflineReceiver extends BroadcastReceiver {
    public static final String TAG = "ForceOfflineReceiver";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG,"18");
        //构建一个对话框
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle("Warning");
        dialogBuilder.setMessage("You are forced to be offline. Please try to login again.");
        //将对话框设为不可取消
        dialogBuilder.setCancelable(false);
        //给对话框注册确定按钮
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //销毁掉所有活动，并重新启动 LoginActivity 这个活动
                ActivityCollector.finishAll();
                Intent intent = new Intent(context, LoginActivity.class);
                //在广播接收器里启动活动的,因此一定要给Intent 加入 FLAG_ACTIVITY_NEW_TASK 这个标志
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 重新启动LoginActivity
                context.startActivity(intent);
            }
        });
        AlertDialog alertDialog = dialogBuilder.create();
        //把对话框的类型设为 TYPE_SYSTEM_ALERT ，不然它将无法在广播接收器里弹出
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        Log.d(TAG,"42");
        alertDialog.show();
    }
}
