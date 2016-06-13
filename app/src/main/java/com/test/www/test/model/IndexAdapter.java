package com.test.www.test.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.test.www.test.R;

import java.util.List;

/**
 * Created by weijingtong20 on 2016/6/13.
 */
public class IndexAdapter extends ArrayAdapter<Index> {
    //ListView 中 item 的布局id
    private int resourceId;
    public IndexAdapter(Context context, int textViewResourceId, List<Index> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Index index = getItem(position); // 获取当前项的 Weather 实例
        View view;
        ViewHolder viewHolder;
        //提升效率，缓存视图
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            //缓存视图空间id ，提升效率
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.value = (TextView) view.findViewById(R.id.value);
            viewHolder.detail = (TextView) view.findViewById(R.id.detail);
            view.setTag(viewHolder); //将ViewHolder 存储在View 中,以便下次直接调用
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(index.getName());
        viewHolder.value.setText(index.getValue());
        viewHolder.detail.setText(index.getDetail());
        return view;
    }
    class ViewHolder {
        TextView name;
        TextView value;
        TextView detail;
    }
}
