package com.test.www.test;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;

    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //--------------全局广播--------------
        /*
//        intentFilter = new IntentFilter();
//        //每当网络状态发生变化时，onReceive()方法就会得到执行
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        networkChangeReceiver = new NetworkChangeReceiver();
//        //注册监听
//        registerReceiver(networkChangeReceiver, intentFilter);

        //自定义广播测试
        Button button = (Button) findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.test.www.test.MY_BROADCAST");
                //标准广播
//                sendBroadcast(intent);
                //有序广播
                sendOrderedBroadcast(intent, null);
            }
        });
*/
        //--------------本地广播--------------
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        Button button = (Button) findViewById(R.id.test);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcasttest.LOCAL_BROADCAST");
                localBroadcastManager.sendBroadcast(intent); //  发送本地广播
            }
        });
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.broadcasttest.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    //全局广播
    class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //通过 getSystemService()方法得到了 ConnectivityManager 的实例
            //这是一个系统服务类， 专门用于管理网络连接的
            ConnectivityManager connectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            // getActiveNetworkInfo()方法可以得到 NetworkInfo 的实例
            NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
            //调用 NetworkInfo 的 isAvailable()方法，就可以判断出当前是否有网络
            if (networkInfo != null && networkInfo.isAvailable()) {
                Toast.makeText(context, "network is available",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "network is unavailable",Toast.LENGTH_SHORT).show();
            }
        }
    }
    //本地广播
    class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "received local broadcast", Toast.LENGTH_SHORT).show();
        }
    }
}
