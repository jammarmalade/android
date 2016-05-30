package com.test.www.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Administrator on 2016/5/28.
 */
public class SecondActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d("MainActivity","Second Task id is "+getTaskId());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.second_layout);
        Button btn_second = (Button)findViewById(R.id.btn_second);
        btn_second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this , ThirdActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 应该为每个活动都添加这样的启动方法，方便调用
     * @param context   启动被活动的上级活动
     * @param data      要传递给本活动的数据
     */
    public static void actionStart(Context context, String data){
        Intent intent = new Intent(context , SecondActivity.class);
        intent.putExtra("data",data);
        context.startActivity(intent);
    }
}
