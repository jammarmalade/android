package com.jam00.www.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.jam00.www.R;
import com.jam00.www.gson.Login;
import com.jam00.www.gson.Tag;
import com.jam00.www.util.ActivityCollector;
import com.jam00.www.util.GlideCircleTransform;
import com.jam00.www.util.HttpUtil;
import com.jam00.www.util.LogUtil;
import com.jam00.www.util.Utility;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by weijingtong20 on 2017/7/24.
 */

public class LoginActivity extends NavBaseActivity implements View.OnClickListener{

    private EditText loginUserName;
    private EditText loginPassword;
    private Button loginBtn;
    private ImageView userHead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        if (Build.VERSION.SDK_INT >= 21) {
            //获取当前活动 DecorView
            View decorView = getWindow().getDecorView();
            //setSystemUiVisibility 改变系统 UI 显示，表示活动布局会显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_common);
        //判断是否登录
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String authkey = prefs.getString("authkey",null);
        if(authkey!=null){
            HomeActivity.actionStart(this);
        }
        String username = prefs.getString("username",null);

        //使用layoutInflater布局
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.content_main);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View contentLayout = layoutInflater.inflate(R.layout.activity_login, null);
        mainLayout.addView(contentLayout);

        toolBarTitle = "登录";
        initNav();

        loginUserName = (EditText)findViewById(R.id.login_username);
        if(username!=null){
            loginUserName.setText(username);
        }
        loginPassword = (EditText)findViewById(R.id.login_password);
        loginBtn = (Button)findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);

        //头像
        userHead = (ImageView)findViewById(R.id.user_head);
        Glide.with(this).load(R.drawable.default_head).transform(new GlideCircleTransform(this)).into(userHead);
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_btn:
                String username = loginUserName.getText().toString();
                if("".equals(username)){
                    mToast("请输入用户名");
                    return ;
                }
                String password = loginPassword.getText().toString();
                if("".equals(password)){
                    mToast("请输入登录密码");
                    return ;
                }
                if(password.length() < 6){
                    mToast("密码最少六位");
                    return ;
                }
                Utility.showProgressDialog(LoginActivity.this,"");
                HashMap<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);
                String url = REQUEST_HOST+"/user/login";
                HttpUtil.postRequest(url, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        BaseActivity.mToastStatic("请求失败");
                        Utility.closeProgressDialog();
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseText = response.body().string();
                        final Login login = (Login) Utility.handleResponse(responseText, Login.class);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (login != null && "true".equals(login.status.toString())) {
                                    //登录成功，保存登录信息
                                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
                                    editor.putString("userid",login.userInfo.id);
                                    editor.putString("username",login.userInfo.username);
                                    editor.putString("authkey",login.userInfo.authkey);
                                    editor.putString("head",login.userInfo.head);
                                    editor.putString("des",login.userInfo.des);
                                    editor.apply();
                                    ActivityCollector.removeActivity(LoginActivity.this);
                                    HomeActivity.actionStart(LoginActivity.this);
                                } else {
                                    BaseActivity.mToastStatic(login.message);
                                }
                                Utility.closeProgressDialog();
                            }
                        });
                    }
                });
                break;
        }
    }

    /**
     * 启动本 活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
