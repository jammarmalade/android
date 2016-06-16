package com.test.www.test;

/**
 * Created by weijingtong20 on 2016/6/16.
 */
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Monkey on 2015/6/29.
 */
public class MyRecyclerViewHolder extends RecyclerView.ViewHolder {

    public TextView mTextView;

    public MyRecyclerViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.id_textview);
    }
}