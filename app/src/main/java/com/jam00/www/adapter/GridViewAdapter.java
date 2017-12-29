package com.jam00.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jam00.www.R;
import com.jam00.www.activity.HomeActivity;

import java.util.List;

/**
 * Created by weijingtong20 on 2017/12/29.
 * 上传图片列表
 */

public class GridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<String> mList;
    private LayoutInflater inflater;

    public GridViewAdapter(Context mContext, List<String> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        //最后多了一个添加图片的ImageView
        int count = mList == null ? 1 : mList.size() + 1;
        if (count > HomeActivity.MAX_IMAGE_NUM) {
            return mList.size();
        } else {
            return count;
        }
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.image_item, parent,false);
        ImageView iv = (ImageView) convertView.findViewById(R.id.pre_img);
        if (position < mList.size()) {
            //代表+号之前的需要正常显示图片
            String picUrl = mList.get(position); //图片路径
            Glide.with(mContext).load(picUrl).into(iv);
        } else {
            iv.setImageResource(R.drawable.ic_add_black);//最后一个显示加号图片
        }
        return convertView;
    }
}
