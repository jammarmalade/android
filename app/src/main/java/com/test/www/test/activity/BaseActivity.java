package com.test.www.test.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.test.www.test.model.ActivityCollector;
import com.test.www.test.model.BaseApplication;
import com.test.www.test.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/11.
 * 所有 Activity 的父类
 */
public class BaseActivity extends Activity {
    public static final String TAG = "BaseActivity";
    private ProgressDialog progressDialog;

//    public static final String REQUEST_HOST = "http://192.168.1.46";//家
    public static final String REQUEST_HOST = "http://192.168.1.10";//公司

    @Override
    protected void onCreate(Bundle sis){
        super.onCreate(sis);
        //加入活动管理器中
        ActivityCollector.addActivity(this);

        //获取当前 Activity 的名称
//        LogUtil.d(TAG, getClass().getSimpleName());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭所以活动
        ActivityCollector.removeActivity(this);
    }

    //显示对话框
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            //点击屏幕其它地方是否会消失
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    //关闭对话框
    public void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
