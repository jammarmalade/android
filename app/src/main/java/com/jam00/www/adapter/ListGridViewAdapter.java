package com.jam00.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jam00.www.R;
import com.jam00.www.activity.BaseActivity;
import com.jam00.www.activity.HomeActivity;
import com.jam00.www.gson.ImageInfo;
import com.jam00.www.util.GlideCircleTransform;
import com.jam00.www.util.LogUtil;

import java.util.List;

/**
 * Created by weijingtong20 on 2017/12/29.
 * 列表图片
 */

public class ListGridViewAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageInfo> mList;
    private LayoutInflater inflater;

    public ListGridViewAdapter(Context mContext, List<ImageInfo> mList) {
        this.mContext = mContext;
        this.mList = mList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
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
        ImageInfo imageInfo = mList.get(position); //图片路径
        //Glide 4.0 新方法 RequestOptions
        RequestOptions options = new RequestOptions();
        options.placeholder(R.drawable.ic_launcher_144);
        Glide.with(mContext).load(imageInfo.thumbUrl).apply(options).into(iv);
        return convertView;
    }
}
