package com.jam00.www.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.jam00.www.custom.MenuPopwindow;
import com.jam00.www.custom.flowtaglayout.FlowTagLayout;
import com.jam00.www.gson.Login;
import com.jam00.www.gson.MenuInfo;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by weijingtong20 on 2017/7/24.
 * 记录列表
 *
 * popwindow 参考 https://www.cnblogs.com/bky1225987336/p/6008538.html
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
    //菜单弹出层
    private MenuPopwindow pw;
    private static final int ACTION_SEARCH_TAG = 1;
    private static final int ACTION_CHOOSE_DATE = 2;
    //每页显示条数
    public static final int RECORD_LIMIT_LIMIT = 10;

    //弹出框
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog addTagDialog;
    //搜索标签的id和日期
    private String searchTagName;
    private int searchTagId;
    private String searchDate;
    private TextView tvSearchDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        //自定义顶部栏
        commonTitle  = (TextView) findViewById(R.id.common_title);
        commonTitle.setText("记录");
        commonBtnLeft = (Button)findViewById(R.id.common_btn_left);
        commonBtnLeft.setVisibility(View.VISIBLE);
        commonBtnLeft.setOnClickListener(this);
        commonBtnRight = (Button)findViewById(R.id.common_btn_right);
        //图标改为三个点
        commonBtnRight.setBackgroundResource(R.drawable.ic_more_vert_white);
        commonBtnRight.setVisibility(View.VISIBLE);
        commonBtnRight.setOnClickListener(this);

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
     * 这个函数在Activity创建完成之后会调用
     * 调用initPopWindow()，则会抛出异常
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            initPopWindow();
        }
    }

    public void initPopWindow(){
        ArrayList<HashMap<String,Object>> menuList = new ArrayList<>();
        HashMap<String,Object> menuInfo = new HashMap<>();
        menuInfo.put("id",ACTION_SEARCH_TAG);
        menuInfo.put("icon",R.drawable.ic_search_white);
        menuInfo.put("title","搜索标签");
        menuList.add(menuInfo);
        menuInfo = (HashMap<String, Object>) menuInfo.clone();
        menuInfo.put("id",ACTION_CHOOSE_DATE);
        menuInfo.put("icon",R.drawable.ic_calendar_white);
        menuInfo.put("title","占位占位");
        menuList.add(menuInfo);
        final List<MenuInfo> list = new ArrayList<>();
        MenuInfo info = null;
        for (int i = 0; i < menuList.size(); i++) {
            menuInfo = menuList.get(i);
            info = new MenuInfo();
            info.setId(Integer.valueOf(menuInfo.get("id").toString()));
            info.setIcon(Integer.valueOf(menuInfo.get("icon").toString()));
            info.setTitle(menuInfo.get("title").toString());
            list.add(info);
        }
        pw = new MenuPopwindow(RecordActivity.this, list);
        pw.setOnItemClick(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (list.get(position).getId()){
                    case ACTION_SEARCH_TAG:
                        //搜索标签逻辑
                        //关闭菜单弹窗
                        pw.showPopupWindow(findViewById(R.id.common_btn_right));
                        //弹出输入框
                        showAddTagDialog();
                        break;
                    case ACTION_CHOOSE_DATE:
                        //选择日期
                        mToast("点我干啥(*^__^*) ");
                        break;
                }
            }
        });
    }
    //搜索弹出框
    private void showAddTagDialog() {
        alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        //自定义标题
        TextView title = new TextView(this);
        title.setText("搜索记录");
        title.setTextSize(18);//字体大小
        title.setPadding(30, 20, 10, 10);//位置
        title.setTextColor(Color.parseColor("#000000"));//颜色
        alertDialogBuilder.setCustomTitle(title);
        //设置内容视图
        final View loginDialog= getLayoutInflater().inflate(R.layout.record_search,null);
        final EditText addTag = (EditText)loginDialog.findViewById(R.id.record_search_tagname);
        //若搜索标签不为空
        if(!"".equals(searchTagName)){
            addTag.setText(searchTagName);
        }
        alertDialogBuilder.setView(loginDialog);
        //绑定选择日期的事件
        tvSearchDate = (TextView)loginDialog.findViewById(R.id.record_search_date);

        //获取当前时间
        Calendar d = Calendar.getInstance(Locale.CHINA);
        Date myDate = new Date();
        d.setTime(myDate);
        final int year = d.get(Calendar.YEAR);
        final int month = d.get(Calendar.MONTH) + 1;
        final int day = d.get(Calendar.DAY_OF_MONTH);
        tvSearchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置日期选择后的显示格式
                DatePickerDialog datePickerDialog = new DatePickerDialog(RecordActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        tvSearchDate.setText(date);
                    }
                },year, month - 1, day);
                datePickerDialog.show();
            }
        });

        //点击搜索标签事件
        alertDialogBuilder.setPositiveButton("查看", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                searchTagName = addTag.getText().toString();
                //日期
                searchDate = tvSearchDate.getText().toString();
                //若标签不为空
                if(!"".equals(searchTagName)){
                    commonTitle.setText("记录 - "+searchTagName);
                }
                page = 1;
                //执行查询
                getRecordData();
            }
        });
        //点击取消事件
        alertDialogBuilder.setNegativeButton("查看全部", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                commonTitle.setText("记录 - ");
                searchTagName = "";
                //日期
                searchDate = "";
                page = 1;
                //执行查询
                getRecordData();
            }
        });

        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(true);
        addTagDialog = alertDialogBuilder.create();
        addTagDialog.show();
    }

    /**
     * 获取用户的记录
     */
    public void getRecordData(){
        String url = REQUEST_HOST+"/record/list";
        HashMap<String ,String> params = new HashMap<>() ;
        params.put("page",String.valueOf(page));
        params.put("limit",String.valueOf(RECORD_LIMIT_LIMIT));
        params.put("tagName",searchTagName);
        params.put("searchDate",searchDate);
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
        mLoadMoreWrapper = new LoadMoreWrapper(recordAdapter,RECORD_LIMIT_LIMIT);
        mLoadMoreWrapper.setLoadMoreView(R.layout.default_loading);
        mLoadMoreWrapper.setOnLoadMoreListener(new LoadMoreWrapper.OnLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if(page == 0 || recordList.size()==0){
                    //没有数据
                    mLoadMoreWrapper.setEndFlag();
                }else{
                    String url = REQUEST_HOST+"/record/list";
                    HashMap<String ,String> params = new HashMap<>() ;
                    params.put("page",String.valueOf(page));
                    params.put("limit",String.valueOf(RECORD_LIMIT_LIMIT));
                    params.put("tagName",searchTagName);
                    params.put("searchDate",searchDate);
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
        //滚动监听，由 mLoadMoreWrapper 代替
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
//                ActivityCollector.removeActivity(this);
//                HomeActivity.actionStart(this,0);
                finish();
                break;
            case R.id.common_btn_right:
                //弹出菜单
                pw.showPopupWindow(findViewById(R.id.common_btn_right));
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        finish();
        return true;
    }

    /**
     * 启动本 活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RecordActivity.class);
        context.startActivity(intent);
    }
}
