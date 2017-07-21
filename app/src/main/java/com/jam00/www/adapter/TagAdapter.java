package com.jam00.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jam00.www.R;
import com.jam00.www.custom.flowtaglayout.OnInitSelectedPosition;
import com.jam00.www.gson.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijingtong20 on 2017/7/19.
 */

public class TagAdapter extends BaseAdapter implements OnInitSelectedPosition {

    private final Context mContext;
    private final List<Tag.info> mDataList;

    public TagAdapter(Context context) {
        this.mContext = context;
        mDataList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.tag_item, null);

        TextView textView = (TextView) view.findViewById(R.id.tv_tag);
        Tag.info t = mDataList.get(position);
        textView.setText(t.name);
        return view;
    }

    public void onlyAddAll(List<Tag.info> datas) {
        mDataList.addAll(datas);
        notifyDataSetChanged();
    }

    public void clearAndAddAll(List<Tag.info> datas) {
        mDataList.clear();
        onlyAddAll(datas);
    }
    public List<Tag.info> getData(){
        return mDataList;
    }
    public String getTagIds(){
        String tids = "",dot = "";
        for(Tag.info tmpInfo : mDataList){
            tids = dot+tmpInfo.id;
            dot = ",";
        }
        return tids;
    }
    public Boolean addDataInfo(Tag.info info){
        Boolean canAdd = true;
        for(Tag.info tmpInfo : mDataList){
            if(tmpInfo.id == info.id){
                canAdd = false;
                break;
            }
        }
        if(canAdd){
            mDataList.add(info);
        }
        return canAdd;
    }
    public void removeDataInfo(int pos){
        mDataList.remove(pos);
    }
    public void notifyDataSet(){
        notifyDataSetChanged();
    }

    @Override
    public boolean isSelectedPosition(int position) {
        if (position % 2 == 0) {
            return true;
        }
        return false;
    }
}

