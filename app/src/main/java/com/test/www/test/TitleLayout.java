package com.test.www.test;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by weijingtong20 on 2016/5/30.\
 * 自定义控件
 */
public class TitleLayout extends LinearLayout{

    public TitleLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        /**
         * inflate d动态加载一个布局文件
         * 第一个参数 加载的布局文件id
         * 第二个参数给加载好的布局添加一个父布局
         */
        LayoutInflater.from(context).inflate(R.layout.title,this);
        //给布局中的按钮注册事件
        Button title_back = (Button)findViewById(R.id.title_back);
        Button title_edit = (Button)findViewById(R.id.title_edit);
        title_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
        title_edit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You clicked Eidt button", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
