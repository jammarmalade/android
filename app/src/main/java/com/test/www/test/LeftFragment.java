package com.test.www.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/5/31.
 */
public class LeftFragment extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //将定义的 left_fragment 布局动态加载进来
        View view = inflater.inflate(R.layout.left_fragment, container , false);
        return view;
    }
}
