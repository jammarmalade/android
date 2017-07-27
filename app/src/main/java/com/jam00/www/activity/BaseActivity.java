package com.jam00.www.activity;

import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jam00.www.R;
import com.jam00.www.util.ActivityCollector;
import com.jam00.www.util.BaseApplication;
import com.jam00.www.util.LogUtil;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public static final String AUTH_KEY = "41b1c534a9284d5785e21ef1e3ac38fe";//获取天气的授权key
    private long mExitTime;

//    public static final String REQUEST_HOST = "http://guolin.tech/api/";//请求地址
    public static final String REQUEST_HOST = "http://192.168.1.100/advanced/api/web/index.php/v1";//本地
    public static final String USER_AUTH_KEY = "a627Q2FAa9H1xmGLOlONEcAtVw8DMnP2ZfMEi80FJzFs5CJY";//用户登录key
    //是否有请求正在执行
    public Boolean httpSending = false;
    //当前活动的名称
    public String activityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //加入活动管理器中
        ActivityCollector.addActivity(this);
        //获取当前 Activity 的名称
        activityName = getClass().getSimpleName();

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭所有活动
        ActivityCollector.removeActivity(this);

    }
    //根据资源id 获取 Bitmap 图片
    public static Bitmap getBitmapFromRes(int resId) {
        Resources res = BaseApplication.getContext().getResources();
        return BitmapFactory.decodeResource(res, resId);
    }
    //获取默认图片，加载之前
    public static Bitmap getPreLoadImg(){
        return BaseActivity.getBitmapFromRes(R.drawable.ic_launcher_144);
    }
    //获取默认头像
    public static Bitmap defaultHeadImg(){
        return BaseActivity.getBitmapFromRes(R.drawable.default_head);
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
