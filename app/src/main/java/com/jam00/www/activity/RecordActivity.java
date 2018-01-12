package com.jam00.www.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jam00.www.R;
import com.jam00.www.adapter.RecordAdapter;
import com.jam00.www.adapter.TagAdapter;
import com.jam00.www.custom.flowtaglayout.FlowTagLayout;
import com.jam00.www.gson.Login;
import com.jam00.www.gson.Record;
import com.jam00.www.gson.Tag;
import com.jam00.www.util.ActivityCollector;
import com.jam00.www.util.BaseApplication;
import com.jam00.www.util.CancelOrOkDialog;
import com.jam00.www.util.GlideCircleTransform;
import com.jam00.www.util.HttpUtil;
import com.jam00.www.util.LoadMoreWrapper;
import com.jam00.www.util.LogUtil;
import com.jam00.www.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by weijingtong20 on 2017/7/24.
 * 记录列表
 */

public class RecordActivity extends BaseActivity implements View.OnClickListener{

    //-------------------通用标题栏-------------------
    public TextView commonTitle;
    public Button commonBtnLeft;
    public Button commonBtnRight;
    public RelativeLayout commonTitleBg;
    private List<Record.info> recordList;
    //记录列表
    private RecyclerView mRecyclerView;
    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private RecordAdapter recordAdapter;

    private Handler handler = new Handler();
    //当前页数
    private int page = 1;
    //加载更多adapter
    private LoadMoreWrapper mLoadMoreWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //自定义顶部栏
        commonTitle  = (TextView) findViewById(R.id.common_title);
        commonTitle.setText("记录");
        commonBtnLeft = (Button)findViewById(R.id.common_btn_left);
        commonBtnLeft.setVisibility(View.VISIBLE);
        commonBtnRight = (Button)findViewById(R.id.common_btn_right);
        commonBtnLeft.setOnClickListener(this);
        commonTitleBg = (RelativeLayout)findViewById(R.id.common_title_bg);
        //getColor(int id)在API23时过时
        commonTitleBg.setBackgroundColor(ContextCompat.getColor(this,R.color.red1));

        //判断是否登录
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String username = prefs.getString("username",null);
        if(username!=null){
            //已登录，执行查询数据
            getRecordData();
        }
    }

    /**
     * 获取用户的记录
     */
    public void getRecordData(){
        String url = REQUEST_HOST+"/record/list";
        HashMap<String ,String> params = new HashMap<>() ;
        params.put("page",String.valueOf(page));
        showProgressDialog(this,"");
        HttpUtil.postRequest(url, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                BaseActivity.mToastStatic("发送失败");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Record record = (Record) Utility.handleResponse(responseText, Record.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (record != null && "true".equals(record.status.toString())) {
                            page = Integer.valueOf(record.message);
                            recordList = record.recordList;
                            //RecyclerView
                            setData();
                        } else {
                            BaseActivity.mToastStatic(record.message);
                        }
                        closeProgressDialog();
                    }
                });
            }
        });
    }
    //渲染数据
    public void setData(){
        mRecyclerView = (RecyclerView) findViewById(R.id.reocrd_layout_RV);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recordAdapter = new RecordAdapter(RecordActivity.this,recordList);
        recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Record.info info = (Record.info)recordList.get(position);
                CancelOrOkDialog dialog = new CancelOrOkDialog(RecordActivity.this, "编辑此条记录？") {
                    @Override
                    public void ok() {
                        super.ok();
                        //关闭 RecordActivity
                        ActivityCollector.finishAll();
//                        ActivityCollector.removeActivity(RecordActivity.this);
                        HomeActivity.actionStart(RecordActivity.this,Integer.valueOf(info.id));
                    }
                };
                dialog.show();
            }
        });
        //创建 加载更多的 view
        mLoadMoreWrapper = new LoadMoreWrapper(recordAdapter);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(page == 0){
                    //没有数据
                }else{
                    String url = REQUEST_HOST+"/record/list";
                    HashMap<String ,String> params = new HashMap<>() ;
                    params.put("page",String.valueOf(page));
                    HttpUtil.postRequest(url, params, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            BaseActivity.mToastStatic("发送失败");
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseText = response.body().string();
                            final Record record = (Record) Utility.handleResponse(responseText, Record.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (record != null && "true".equals(record.status.toString())) {
                                        page = Integer.valueOf(record.message);
                                        if(page == 0){
                                            mLoadMoreWrapper.setEndFlag();
                                        }
                                        recordList.addAll(record.recordList);
                                        mLoadMoreWrapper.notifyDataSetChanged();
                                    } else {
                                        BaseActivity.mToastStatic(record.message);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
        mRecyclerView.setAdapter(mLoadMoreWrapper);
        //滚动监听，由mLoadMoreWrapper 代替
//        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if(newState == RecyclerView.SCROLL_STATE_IDLE){
//                    int lastVisiblePosition = linearLayoutManager.findLastVisibleItemPosition();
//                    if(lastVisiblePosition >= linearLayoutManager.getItemCount() - 1){
//                        loadMoreRecord();
//                    }
//                }
//            }
//        });
    }
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.common_btn_left:
                ActivityCollector.removeActivity(this);
                HomeActivity.actionStart(this,0);
                break;
        }
    }

    /**
     * 启动本 活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RecordActivity.class);
        context.startActivity(intent);
    }
}
