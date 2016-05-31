package com.test.www.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by weijingtong20 on 2016/5/31.
 */
public class FruitAdapter extends ArrayAdapter<Fruit> {
    private int resourceId;
    public FruitAdapter(Context context, int textViewResourceId, List<Fruit> objects){
        super(context , textViewResourceId , objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position , View convertView, ViewGroup parent){
        //获取当前项的 Fruit 实例
        Fruit fruit = getItem(position);
        //为这个实例布局
        //convertView 是之前加载好的布局文件缓存，第二次可直接使用
        View view;
        ViewHolder viewHolder;//避免每次都去获取一遍控件实例
        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId , null);
            viewHolder = new ViewHolder();
            viewHolder.fruitImage = (ImageView)view.findViewById(R.id.fruit_image);
            viewHolder.fruitName = (TextView)view.findViewById(R.id.fruit_name);
            //将 ViewHolder 存储在 View 中
            view.setTag(viewHolder);
        }else{
            view = convertView;
            //重新获取 viewHolder
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.fruitImage.setImageResource(fruit.getImageId());
        viewHolder.fruitName.setText(fruit.getName());
        return view;
    }
    class ViewHolder{
        ImageView fruitImage;
        TextView fruitName;
    }
}
