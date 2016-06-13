package com.test.www.test.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.www.test.R;

import java.util.List;

/**
 * Created by weijingtong20 on 2016/6/13.
 */
public class ForecastAdapter extends ArrayAdapter<Weather> {
    //ListView 中 item 的布局id
    private int resourceId;
    public ForecastAdapter(Context context, int textViewResourceId, List<Weather> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Weather weather = getItem(position); // 获取当前项的 Weather 实例
        View view;
        ViewHolder viewHolder;
        //提升效率，缓存视图
        if(convertView==null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            //缓存视图空间id ，提升效率
            viewHolder = new ViewHolder();
            viewHolder.date = (TextView) view.findViewById(R.id.date);
            viewHolder.type = (TextView) view.findViewById(R.id.type);
            viewHolder.tmpLow = (TextView) view.findViewById(R.id.tmpLow);
            viewHolder.tmpHigh = (TextView) view.findViewById(R.id.tmpHigh);
            view.setTag(viewHolder); //将ViewHolder 存储在View 中,以便下次直接调用
        }else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.date.setText(weather.getDate());
        viewHolder.type.setText(weather.getType());
        viewHolder.tmpLow.setText(weather.getTmpLow());
        viewHolder.tmpHigh.setText(weather.getTmpHigh());
        return view;
    }
    class ViewHolder {
        TextView date;
        TextView type;
        TextView tmpLow;
        TextView tmpHigh;
    }
}
