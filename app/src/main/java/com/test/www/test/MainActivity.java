package com.test.www.test;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.io.File;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private Button sendNotice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        sendNotice = (Button) findViewById(R.id.send_notice);
        sendNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.send_notice:
                        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification;
                        //点击通知的意图
                        Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                        PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//                        if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN){
//                            notification = new Notification(R.mipmap.ic_launcher, "This is ticker text", System.currentTimeMillis());
//                            notification.setLatestEventInfo(MainActivity.this, "This is content title", "This is content text", null);
//                        }else{
                        //sdk 6.0(23) 写法
                        Notification.Builder builder = new Notification.Builder(MainActivity.this);
                        //瞬时显示的内容
                        builder.setTicker("This is ticker text");
                        //通知标题
                        builder.setContentTitle("This is content title");
                        //通知内容
                        builder.setContentText("This is content text");
                        //通知图标
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        //通知的声音
//                        Uri soundUri = Uri.fromFile(new File("/system/media/audio/ringtones/Basic_tone.ogg"));
//                        builder.setSound(soundUri);
                        /** 设置默认的提醒方式
                         DEFAULT_ALL：铃声、闪光、震动均系统默认。
                         DEFAULT_SOUND：系统默认铃声。
                         DEFAULT_VIBRATE：系统默认震动。
                         DEFAULT_LIGHTS：系统默认闪光。
                         */
                        builder.setDefaults(Notification.DEFAULT_SOUND);//默认声音
                        //手机震动
                        //让手机在通知到来的时候立刻振动 1 秒，然后静 止 1 秒，再振动 1 秒
                        //需要添加权限 <uses-permission android:name="android.permission.VIBRATE" />
                        long[] vibrates = {0, 1000, 1000, 1000};
                        builder.setVibrate(vibrates);
                        /** 通知  LED 灯
                         ledARGB 用于控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
                         ledOnMS 用于指定 LED 灯亮起的时长，以毫秒为单位
                         ledOffMS 用于指定 LED 灯暗去的时长，也是以毫秒为单位
                         flags 可用于指定通知的一些行为
                         实现 LED 灯以绿色的灯光一闪一闪的效果
                         */
                        builder.setLights(Color.RED,1000,1000);

                        //点击通知的意图
                        builder.setContentIntent(pi);
                        //创建通知
                        notification = builder.build();
//                        }

                        manager.notify(1, notification);
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
