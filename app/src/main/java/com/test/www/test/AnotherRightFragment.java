package com.test.www.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/5/31.
 */
public class AnotherRightFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.another_right_fragment,container, false);
        //调用 getActivity()方法来得到和当前碎片相关联的活动实例
        MainActivity activity = (MainActivity) getActivity();
        return view;
    }
}
