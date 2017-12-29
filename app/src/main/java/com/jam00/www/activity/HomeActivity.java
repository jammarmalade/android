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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog.OnDateSetListener;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.Text;
import com.jam00.www.R;
import com.jam00.www.adapter.GridViewAdapter;
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
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

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
    //记录显示标题
    private TextView recordTitle;
    //lbs
    private LbsUtil lbsUtil;
    private String latitude;//纬度
    private String longitude;//经度
    //记录类型
    private int recordType = 1 ;
    private Button inBtn;
    private Button outBtn;
    private Button recordBtn;
    private TextView showType;
    private LinearLayout accountWriteArea;//输入金额的区域
    private LinearLayout recordTitleArea;//记录提示的标题区域
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
    //当前标签列表页数
    private int page = 1;
    //是否没有下一页标签数据
    private boolean tagEnd = false;
    //下一个和上一页按钮
    private TextView preTag;
    private TextView nextTag;
    //显示要上传的图片
    private GridView showImageGridView;
    private ArrayList<String> mPicList = new ArrayList<>(); //上传的图片凭证的数据源
    private GridViewAdapter mGridViewAddImgAdapter; //展示上传的图片的适配器
    public static final int MAX_IMAGE_NUM = 9;//最多选择9张图片
    //startActivityForResult 请求码
    public static final int REQUEST_CODE = 10;
    public static final int RESULT_CODE_VIEW_IMG = 11; //查看大图页面的结果码

    public static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //沉浸式状态栏
//        if (Build.VERSION.SDK_INT >= 21) {
//            //获取当前活动 DecorView
//            View decorView = getWindow().getDecorView();
//            //setSystemUiVisibility 改变系统 UI 显示，表示活动布局会显示在状态栏上
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            //设置状态栏为透明色
//            getWindow().setStatusBarColor(Color.TRANSPARENT);
//        }
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
        //标签上一页和下一页选择
        preTag = (TextView)findViewById(R.id.pre_tag);
        preTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTagList(v);
            }
        });
        nextTag = (TextView)findViewById(R.id.next_tag);
        nextTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTagList(v);
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
                //设置日期选择后的显示格式
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
        recordBtn = (Button)findViewById(R.id.record_type_none);
        showType = (TextView)findViewById(R.id.show_type);
        //显示记录标题的区域
        recordTitleArea = (LinearLayout)findViewById(R.id.record_title_area) ;
        //点击支出
        outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordType = 1;
                inBtn.setTextColor(getResources().getColor(R.color.grey));
                outBtn.setTextColor(getResources().getColor(R.color.red));
                recordBtn.setTextColor(getResources().getColor(R.color.grey));
                recordTitleArea.setVisibility(View.GONE);
                showType.setText(outBtn.getText());
                accountWriteArea.setBackgroundColor(getResources().getColor(R.color.recordOut));
                accountWriteArea.setVisibility(View.VISIBLE);
            }
        });
        //点击收入
        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordType = 2;
                outBtn.setTextColor(getResources().getColor(R.color.grey));
                inBtn.setTextColor(getResources().getColor(R.color.red));
                recordBtn.setTextColor(getResources().getColor(R.color.grey));
                recordTitleArea.setVisibility(View.GONE);
                showType.setText(inBtn.getText());
                accountWriteArea.setBackgroundColor(getResources().getColor(R.color.recordIn));
                accountWriteArea.setVisibility(View.VISIBLE);
            }
        });
        recordTitle = (TextView)findViewById(R.id.show_record_title);
        //点击记录
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordType = 0;
                outBtn.setTextColor(getResources().getColor(R.color.grey));
                inBtn.setTextColor(getResources().getColor(R.color.grey));
                recordBtn.setTextColor(getResources().getColor(R.color.red));
                accountWriteArea.setVisibility(View.GONE);
                recordTitle.setText(getRecordTitle());
                recordTitleArea.setVisibility(View.VISIBLE);
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
        //显示图片的区域
//        showImageGridView = (GridView)findViewById(R.id.select_image_area);
//        initGridView();
        //提交按钮
        submitBtn = (Button)findViewById(R.id.submit_record);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOrEditRecord();
            }
        });
    }

    /**
     * 随机生成记录的提示文字
     */
    private String getRecordTitle(){
        String[] titleArray = new String[]{
                "发生了什么新鲜事",
                "没事记一下",
                "今天有啥新闻",
                "来写一篇日记吧"
        };
        int max=titleArray.length;
        int min=0;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return titleArray[s];
    }

    /**
     * 获取推荐标签
     */
    private void getTagList(View v){
        if(R.id.pre_tag==v.getId()){
            //上一页
            if(page <= 1){
                BaseActivity.mToastStatic("已经是第一页了");
            }else{
                page = page - 1;
            }
        }else{
            if(tagEnd){
                BaseActivity.mToastStatic("没有数据了");
            }else{
                page = page + 1;
            }
        }
        getShowTagData();
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
        if(recordType==0 && "".equals(remark.getText().toString())){
            mToast("请输入记录内容");
            return ;
        }
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
        if(recordType!=0 && "".equals(postParams.get("account"))){
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
        String tagUrl = REQUEST_HOST + "/tag/recommend?page="+page;
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
                            showTagAdapter.clearAndAddAll(tag.tagList);
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
    //-------------------------------------显示图片区域----------------------------------
    /**
     * 初始化显示图片区域
     */
    private void initGridView(){
        mGridViewAddImgAdapter = new GridViewAdapter(this, mPicList);
        showImageGridView.setAdapter(mGridViewAddImgAdapter);
        showImageGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == parent.getChildCount() - 1) {
                    //如果“增加按钮形状的”图片的位置是最后一张，且添加了的图片的数量不超过9张，才能点击
                    if (mPicList.size() == MAX_IMAGE_NUM) {
                        //最多添加 9 张图片
                        viewPluImg(position);
                    } else {
                        //添加凭证图片
                        selectPic(MAX_IMAGE_NUM - mPicList.size());
                    }
                } else {
                    viewPluImg(position);
                }
            }
        });
    }

    /**
     * 点击查看大图
     * @param position
     */
    private void viewPluImg(int position) {
        Intent intent = new Intent(this, AddImageActivity.class);
        intent.putStringArrayListExtra("img_list", mPicList);
        intent.putExtra("position", position);
        startActivityForResult(intent, REQUEST_CODE);
    }
    /**
     * 打开相册或者照相机选择图片，最多9张
     *
     * @param maxTotal 最多选择的图片的数量
     */
    private void selectPic(int maxTotal) {
//        PictureSelectorConfig.initMultiConfig(this, maxTotal);
        PictureSelector.create(HomeActivity.this).openGallery(PictureMimeType.ofImage()).maxSelectNum(maxTotal).isCamera(true)
                .compress(true);
    }

    // 处理选择的照片的地址
    private void refreshAdapter(List<LocalMedia> picList) {
        for (LocalMedia localMedia : picList) {
            //被压缩后的图片路径
            if (localMedia.isCompressed()) {
                String compressPath = localMedia.getCompressPath(); //压缩后的图片路径
                mPicList.add(compressPath); //把图片添加到将要上传的图片数组中
                mGridViewAddImgAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    refreshAdapter(PictureSelector.obtainMultipleResult(data));
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
                    break;
            }
        }
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE_VIEW_IMG) {
            //查看大图页面删除了图片
            ArrayList<String> toDeletePicList = data.getStringArrayListExtra("img_list"); //要删除的图片的集合
            mPicList.clear();
            mPicList.addAll(toDeletePicList);
            mGridViewAddImgAdapter.notifyDataSetChanged();
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
