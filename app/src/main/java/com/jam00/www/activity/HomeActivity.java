package com.jam00.www.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.jam00.www.R;
import com.jam00.www.adapter.TagAdapter;
import com.jam00.www.custom.flowtaglayout.FlowTagLayout;
import com.jam00.www.custom.flowtaglayout.OnTagClickListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends NavBaseActivity {
    //测试
    private FlowTagLayout checkFlowTagLayout;
    private TagAdapter<String> checkTagAdapter;

    public static final String TAG = "HomeActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
        if(Build.VERSION.SDK_INT >= 21){
            //获取当前活动 DecorView
            View decorView = getWindow().getDecorView();
            //setSystemUiVisibility 改变系统 UI 显示，表示活动布局会显示在状态栏上
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            //设置状态栏为透明色
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_home);

        toolBarTitle = "首页";
        initNav();


        //测试
        checkFlowTagLayout = (FlowTagLayout) findViewById(R.id.check_flow_layout);
        //颜色
        checkTagAdapter = new TagAdapter<>(this);
        checkFlowTagLayout.setAdapter(checkTagAdapter);
        checkFlowTagLayout.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onItemClick(FlowTagLayout parent, View view, int position) {
                mToast("删除 - "+parent.getAdapter().getItem(position));
            }
        });
        initCheckData();

    }

    private void initCheckData() {
        List<String> dataSource = new ArrayList<>();
        dataSource.add("红色");
        dataSource.add("黑色");
        dataSource.add("花边色");
        dataSource.add("深蓝色");
        dataSource.add("白色");
        dataSource.add("玫瑰红色");
        dataSource.add("紫黑紫兰色");
        dataSource.add("葡萄红色");
        dataSource.add("屎黄色");
        dataSource.add("绿色");
        dataSource.add("彩虹色");
        dataSource.add("牡丹色");
        checkTagAdapter.onlyAddAll(dataSource);
    }


    /**
     * 启动本 活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }
}
