package com.jam00.www.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog.OnDateSetListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.jam00.www.R;
import com.jam00.www.adapter.TagAdapter;
import com.jam00.www.custom.flowtaglayout.FlowTagLayout;
import com.jam00.www.custom.flowtaglayout.OnTagClickListener;
import com.jam00.www.gson.Result;
import com.jam00.www.gson.Tag;
import com.jam00.www.gson.TagInfo;
import com.jam00.www.util.BaseApplication;
import com.jam00.www.util.HttpUtil;
import com.jam00.www.util.LbsUtil;
import com.jam00.www.util.LogUtil;
import com.jam00.www.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.jam00.www.util.Utility.closeProgressDialog;

public class HomeActivity extends NavBaseActivity {
    //标签
    private FlowTagLayout checkFlowTagLayout;
    private TagAdapter checkTagAdapter;
    private FlowTagLayout showFlowTagLayout;
    private TagAdapter showTagAdapter;
    //增加标签
    private TextView addTag;
    //弹出框
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog addTagDialog;
    //日期选择
    private TextView recordDate;
    //地址显示
    private TextView recordLocation;
    //lbs
    private LbsUtil lbsUtil;
    private String latitude;//纬度
    private String longitude;//经度
    //记录类型
    private int recordType = 1 ;
    private Button inBtn;
    private Button outBtn;
    private TextView showType;
    private LinearLayout accountWriteArea;//输入金额的区域
    //提交按钮
    private Button submitBtn;
    //提交的数据
    private HashMap<String,String> postParams = new HashMap<>();
    //备注
    private EditText remark;
    //金额
    private EditText money;
    //地址信息
    private Map<String,String> locationInfo = new HashMap<String,String>();

    public static final String TAG = "HomeActivity";

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
        //使用layoutInflater布局
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.content_main);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View contentLayout = layoutInflater.inflate(R.layout.activity_home, null);
        mainLayout.addView(contentLayout);

        toolBarTitle = "记录点滴";
        initNav();

        //选中的标签
        checkFlowTagLayout = (FlowTagLayout) findViewById(R.id.check_flow_layout);
        checkTagAdapter = new TagAdapter(this);
        checkFlowTagLayout.setAdapter(checkTagAdapter);
        checkFlowTagLayout.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onItemClick(FlowTagLayout parent, View view, int position) {
                checkTagAdapter.removeDataInfo(position);
                checkTagAdapter.notifyDataSet();
            }
        });
        //待选的标签
        showFlowTagLayout = (FlowTagLayout) findViewById(R.id.show_flow_layout);
        showTagAdapter = new TagAdapter(this);
        showFlowTagLayout.setAdapter(showTagAdapter);
        showFlowTagLayout.setOnTagClickListener(new OnTagClickListener() {
            @Override
            public void onItemClick(FlowTagLayout parent, View view, int position) {
                if (checkTagAdapter.getCount() >= 10) {
                    mToast("最多只能选十个标签");
                } else {
                    if(checkTagAdapter.addDataInfo((TagInfo) parent.getAdapter().getItem(position))){
                        checkTagAdapter.notifyDataSet();
                    }else{
                        mToast("该标签已存在");
                    }
                }
            }
        });
        getShowTagData();

        //增加标签，弹出输入框
        addTag = (TextView) findViewById(R.id.add_tag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTagDialog();
            }
        });
        //日期选择
        recordDate = (TextView)findViewById(R.id.record_date);
        Calendar d = Calendar.getInstance(Locale.CHINA);
        // 创建一个日历引用d，通过静态方法getInstance() 从指定时区 Locale.CHINA 获得一个日期实例
        Date myDate = new Date();
        // 创建一个Date实例
        d.setTime(myDate);
        // 设置日历的时间，把一个新建Date实例myDate传入
        final int year = d.get(Calendar.YEAR);
        final int month = d.get(Calendar.MONTH) + 1;
        final int day = d.get(Calendar.DAY_OF_MONTH);
        recordDate.setText(year + "-" + month + "-" + day);
        recordDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(HomeActivity.this, new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        recordDate.setText(date);
                        mToast(date);
                    }
                },year, month - 1, day);
                datePickerDialog.show();
            }
        });
        //地址显示
        recordLocation = (TextView)findViewById(R.id.record_location);
        //获取定位信息
        lbsUtil = new LbsUtil();
        lbsUtil.getLocation(HomeActivity.this, new MyLocationListener());
        //记录类型
        accountWriteArea = (LinearLayout)findViewById(R.id.account_write_area) ;
        outBtn = (Button)findViewById(R.id.record_type_out);
        inBtn = (Button)findViewById(R.id.record_type_in);
        showType = (TextView)findViewById(R.id.show_type);
        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordType = 1;
                inBtn.setTextColor(getResources().getColor(R.color.grey));
                outBtn.setTextColor(getResources().getColor(R.color.red));
                showType.setText(outBtn.getText());
                accountWriteArea.setBackgroundColor(getResources().getColor(R.color.recordOut));
            }
        });
        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordType = 2;
                outBtn.setTextColor(getResources().getColor(R.color.grey));
                inBtn.setTextColor(getResources().getColor(R.color.red));
                showType.setText(inBtn.getText());
                accountWriteArea.setBackgroundColor(getResources().getColor(R.color.recordIn));
            }
        });
        remark = (EditText)findViewById(R.id.record_remark);
        money = (EditText)findViewById(R.id.record_money);
        //限制只能输入小数点后两位
        money.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String temp = s.toString();
                int posDot = temp.indexOf(".");
                if (posDot <= 0) return;
                if (temp.length() - posDot - 1 > 2) {
                    s.delete(posDot + 3, posDot + 4);
                }
            }
        });
        //提交按钮
        submitBtn = (Button)findViewById(R.id.submit_record);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrEditRecord();
            }
        });
    }

    //添加标签弹出框
    private void showAddTagDialog() {
        alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
        //自定义标题
        TextView title = new TextView(this);
        title.setText("新增标签");
        title.setTextSize(18);//字体大小
        title.setPadding(30, 20, 10, 10);//位置
        title.setTextColor(Color.parseColor("#000000"));//颜色
        alertDialogBuilder.setCustomTitle(title);
        //设置内容视图
        final View loginDialog= getLayoutInflater().inflate(R.layout.add_tag,null);
        alertDialogBuilder.setView(loginDialog);
//        alertDialogBuilder.setMessage("内容");

        //点击添加标签事件
        alertDialogBuilder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText addTag = (EditText)loginDialog.findViewById(R.id.add_tag_name);
                String addTagName = addTag.getText().toString();
                if (checkTagAdapter.getCount() >= 10) {
                    mToast("最多只能选十个标签");
                } else {
                    Utility.showProgressDialog(HomeActivity.this,"");
                    //添加标签
                    String url = REQUEST_HOST+"/tag/addtag";
                    HashMap<String ,String> params = new HashMap<String, String>() ;
                    params.put("name",addTagName);
                    HttpUtil.postRequest(url, params, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            BaseActivity.mToastStatic("发送失败");
                            Utility.closeProgressDialog();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            final String responseText = response.body().string();
                            final Tag tag = (Tag) Utility.handleResponse(responseText,Tag.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (tag != null && "true".equals(tag.status.toString())) {
                                        if(checkTagAdapter.addDataInfo(tag.tagList.get(0))){
                                            checkTagAdapter.notifyDataSet();
                                        }else{
                                            mToast("该标签已存在");
                                        }
                                    } else {
                                        BaseActivity.mToastStatic(tag.message);
                                    }
                                    Utility.closeProgressDialog();
                                }
                            });
                        }
                    });
                }
            }
        });
        //点击取消事件
        alertDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        //设置对话框是可取消的
        alertDialogBuilder.setCancelable(true);
        addTagDialog = alertDialogBuilder.create();
        addTagDialog.show();
    }
    //提交记录
    public void addOrEditRecord(){
        if(httpSending){
            return ;
        }
        String url = REQUEST_HOST+"/record/add";
        postParams.put("type",String.valueOf(recordType));
        //标签id
        postParams.put("tids",checkTagAdapter.getTagIds());
        //备注
        postParams.put("content",remark.getText().toString());
        //金额
        postParams.put("account",money.getText().toString());
        //当前经纬度地址
        postParams.put("longitude",longitude);
        postParams.put("latitude",latitude);
        postParams.put("country",locationInfo.get("country"));
        postParams.put("province",locationInfo.get("province"));
        postParams.put("city",locationInfo.get("city"));
        postParams.put("area",locationInfo.get("district"));
        postParams.put("address",locationInfo.get("street"));
        //日期
        postParams.put("date",recordDate.getText().toString());
        if("".equals(postParams.get("account"))){
            mToast("请输入金额");
            return ;
        }
        if(checkTagAdapter.getCount() <= 0){
            mToast("请至少选择一个标签");
            return ;
        }

        httpSending = true;
        Utility.showProgressDialog(HomeActivity.this,"");
        HttpUtil.postRequest(url, postParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpSending = false;
                e.printStackTrace();
                //在主线程中执行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseActivity.mToastStatic("提交失败");
                        Utility.closeProgressDialog();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpSending = false;
                final String responseText = response.body().string();
                final Result res = (Result) Utility.handleResponse(responseText, Result.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res != null && "true".equals(res.status.toString())) {
                            BaseActivity.mToastStatic("提交成功");
                            remark.setText("");
                            money.setText("");
                            //初始化选择标签
                            checkTagAdapter.clearAndAddAll(new ArrayList<TagInfo>());
                            money.requestFocus();
                        } else {
                            BaseActivity.mToastStatic(res.message);
                        }
                        Utility.closeProgressDialog();
                    }
                });
            }
        });
    }

    //获取常用的标签
    private void getShowTagData() {
        String tagUrl = REQUEST_HOST + "/tag/recommend";
        HttpUtil.postRequest(tagUrl,new HashMap<String, String>() ,new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                //在主线程中执行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BaseActivity.mToastStatic("获取信息失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Tag tag = (Tag) Utility.handleResponse(responseText, Tag.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (tag != null && "true".equals(tag.status.toString())) {
                            showTagAdapter.onlyAddAll(tag.tagList);
                        } else {
                            BaseActivity.mToastStatic(tag.message);
                        }
                    }
                });
            }
        });
    }
    /**
     * 监听器，定位结果会回调
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(final BDLocation location){
            String type = "";
            if(location.getLocType()==BDLocation.TypeGpsLocation){
                type = "GPS";
            }else if(location.getLocType()==BDLocation.TypeNetWorkLocation){
                type = "网络";
            }
            String finaltype = type;

            locationInfo.put("latitude",Double.toString(location.getLatitude()));
            locationInfo.put("longitude",Double.toString(location.getLongitude()));
            locationInfo.put("type",finaltype);
            locationInfo.put("country",location.getCountry());
            locationInfo.put("province",location.getProvince());
            locationInfo.put("city",location.getCity());
            locationInfo.put("district",location.getDistrict());
            locationInfo.put("street",location.getStreet()+location.getStreetNumber());

            latitude = locationInfo.get("latitude");
            longitude = locationInfo.get("longitude");
            //获取详细地址信息
            String s = locationInfo.get("country")+locationInfo.get("province")+locationInfo.get("city")+locationInfo.get("district")+locationInfo.get("street");
            recordLocation.setText(s);
        }
    }

    /**
     * 启动本 活动
     */
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }
}
