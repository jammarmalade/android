package com.test.www.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Administrator on 2016/5/28.
 */
public class SecondActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.second_layout);
        //接收上一个活动传递过来的值
//        final Intent intent = getIntent();
//        String data = intent.getStringExtra("extra_data");
//        Log.d("SecondActivity",data);
        //绑定单击事件，并返回数据给上一个活动
        Button btn2 = (Button)findViewById(R.id.btn_2);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("data_return", "Hello MainActivity");
                //用于向上一个活动返回数据
                setResult(RESULT_OK, intent1);
                finish();
            }
        });
    }

    /**
     * 用户点击back键返回时也返回数据
     */
    @Override
    public void onBackPressed(){
        Intent intent1 = new Intent();
        intent1.putExtra("data_return", "Hello MainActivity from back");
        //用于向上一个活动返回数据
        setResult(RESULT_OK, intent1);
        finish();
    }
}
