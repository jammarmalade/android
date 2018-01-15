package com.jam00.www.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
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

import java.lang.ref.WeakReference;

public class BaseActivity extends AppCompatActivity {

    public static final String TAG = "BaseActivity";
    public static final String AUTH_KEY = "41b1c534a9284d5785e21ef1e3ac38fe";//获取天气的授权key
    private long mExitTime;

//    public static final String REQUEST_HOST = "http://guolin.tech/api/";//请求地址
//    public static final String REQUEST_HOST = "http://192.168.1.136/advanced/api/web/index.php/v1";//本地
    public static final String REQUEST_HOST = "http://api.jam00.com/v1";//线上
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

    //----------------------------------------加载、请求框--------------------------------------
    private static ProgressDialog progressDialog;
    private static WeakReference<Activity> mWeakReference;
    //显示对话框
    public static void showProgressDialog(Activity activity, String msg) {
        if(!isLiving(activity)){
            return;
        }
        if(mWeakReference == null){
            mWeakReference = new WeakReference(activity);
        }

        activity = mWeakReference.get();
        if (progressDialog == null) {
            if (activity.getParent() != null) {
                progressDialog = new ProgressDialog(activity.getParent());
            } else {
                progressDialog = new ProgressDialog(activity);
            }
        }
        if("".equals(msg)){
            msg = "正在请求...";
        }
        progressDialog.setMessage(msg);
        if (!progressDialog.isShowing()) {
            progressDialog.dismiss();
            //点击屏幕其它地方是否会消失
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    /**
     * 判断进度框是否正在显示
     */
    private static boolean isShowing(ProgressDialog dialog) {
        boolean isShowing = dialog != null
                && dialog.isShowing();
//        LogUtil.d(TAG,">------isShow:"+isShowing);
        return isShowing;
    }
    //关闭对话框
    public static void closeProgressDialog() {
        if (isShowing(progressDialog) && isExistLiving(mWeakReference)) {
            progressDialog.dismiss();
            progressDialog = null;
            mWeakReference.clear();
            mWeakReference = null;
        }
    }
    private static boolean isExistLiving(WeakReference<Activity> weakReference) {
        if(weakReference != null){
            Activity activity = weakReference.get();
            if (activity == null) {
                return false;
            }
            if (activity.isFinishing()) {
                return false;
            }
            return true;
        }
        return false;
    }
    //判断Activity是否存活
    private static boolean isLiving(Activity activity) {
        if (activity == null) {
            LogUtil.d(TAG, "activity == null");
            return false;
        }
        if (activity.isFinishing()) {
            LogUtil.d(TAG, "activity is finishing");
            return false;
        }
        return true;
    }

}
