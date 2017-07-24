package com.jam00.www.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.jam00.www.util.BaseApplication;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public static final String AUTH_KEY = "41b1c534a9284d5785e21ef1e3ac38fe";//获取天气的授权key
    private long mExitTime;

//    public static final String REQUEST_HOST = "http://guolin.tech/api/";//请求地址
    public static final String REQUEST_HOST = "http://192.168.1.100/advanced/api/web/index.php/v1";//本地
    public static final String USER_AUTH_KEY = "a627Q2FAa9H1xmGLOlONEcAtVw8DMnP2ZfMEi80FJzFs5CJY";//用户登录key


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    //按两次退出程序
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //是在首页
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mToast("再按一次退出程序");
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //弹出消息
    public void mToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    //弹出消息
    public static void mToastStatic(String msg){
        Toast.makeText(BaseApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
