package com.jam00.www.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jam00.www.R;
import com.jam00.www.adapter.ViewPagerAdapter;
import com.jam00.www.util.CancelOrOkDialog;
import com.jam00.www.util.LogUtil;
import com.jam00.www.util.StatusBarUtil;

import java.util.ArrayList;

/**
 * Created by weijingtong20 on 2017/12/29.
 * 选择图片的页面
 */

public class AddImageActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener{
    private ViewPager viewPager; //展示图片的ViewPager
    private TextView positionTv; //图片的位置，第几张图片
    private ArrayList<String> imgList; //图片的数据源
    private int mPosition; //第几张图片
    private ViewPagerAdapter mAdapter;
    private boolean showDelete;//是否显示删除按钮
    private ImageView deleteIM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image);
        //修改状态栏颜色
        StatusBarUtil.setWindowStatusBarColor(this,R.color.black);

        imgList = getIntent().getStringArrayListExtra("img_list");
        mPosition = getIntent().getIntExtra("position", 0);
        showDelete = getIntent().getBooleanExtra("showDel", true);
        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        positionTv = (TextView) findViewById(R.id.position_tv);
        findViewById(R.id.back_iv).setOnClickListener(this);
        deleteIM = (ImageView) findViewById(R.id.delete_iv);
        if(showDelete){
            deleteIM.setOnClickListener(this);
        }else{
            deleteIM.setVisibility(View.GONE);
        }
        viewPager.addOnPageChangeListener(this);

        mAdapter = new ViewPagerAdapter(this, imgList);
        viewPager.setAdapter(mAdapter);
        positionTv.setText(mPosition + 1 + "/" + imgList.size());
        viewPager.setCurrentItem(mPosition);
    }

    //删除图片
    private void deletePic() {
        CancelOrOkDialog dialog = new CancelOrOkDialog(this, "要删除这张图片吗?") {
            @Override
            public void ok() {
                super.ok();
                imgList.remove(mPosition); //从数据源移除删除的图片
                setPosition();
                dismiss();
            }
        };
        dialog.show();
    }

    //设置当前位置
    private void setPosition() {
        positionTv.setText(mPosition + 1 + "/" + imgList.size());
        viewPager.setCurrentItem(mPosition);
        mAdapter.notifyDataSetChanged();
    }

    //返回上一个页面
    private void back() {
        Intent intent = getIntent();
        intent.putStringArrayListExtra("img_list", imgList);
        setResult(HomeActivity.RESULT_CODE_VIEW_IMG, intent);
        finish();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mPosition = position;
        positionTv.setText(position + 1 + "/" + imgList.size());
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                //返回
                back();
                break;
            case R.id.delete_iv:
                //删除图片
                deletePic();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //按下了返回键
            back();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
