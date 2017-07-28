package com.jam00.www.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jam00.www.R;
import com.jam00.www.custom.flowtaglayout.FlowTagLayout;
import com.jam00.www.gson.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by weijingtong20 on 2017/7/26.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context mContext;
    private List<Record.info> dataList;
    //标签
    private TagAdapter showTagAdapter;
    private String account;


    public RecordAdapter(Context c , List<Record.info> dataList){
        super();
        this.mContext = c;
        this.dataList = dataList;

    }

    public List<Record.info> getmDataList(){
        return dataList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        switch (viewType){
            case 1:
                View view = LayoutInflater.from(mContext).inflate(R.layout.record_list_item, parent, false);
                listHolder listHolder = new listHolder(view);
                holder = listHolder;
                view.setOnClickListener(this);
                break;
        }
        return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        Record.info recordInfo = dataList.get(position);
        switch (getItemViewType(position)){
            case 1:
                //设置数据
                final listHolder listHolder = (listHolder)holder;
                //金额
                if(recordInfo.type == 1){
                    account = " - "+recordInfo.account;
                    listHolder.recordAccount.setText(account);
                }
                if(recordInfo.type == 2){
                    account = " + "+recordInfo.account;
                    listHolder.recordAccount.setText(account);
                    listHolder.recordAccount.setTextColor(ContextCompat.getColor(mContext,R.color.green1));
                }
                //记录时间
                listHolder.recordCreateTime.setText(recordInfo.showTime);
                //备注内容
                listHolder.recordContent.setText(recordInfo.content);
                //显示标签
                showTagAdapter = new TagAdapter(mContext);
                listHolder.showTagLayout.setAdapter(showTagAdapter);
                showTagAdapter.onlyAddAll(recordInfo.tagList);
                listHolder.itemView.setTag(position);
                break;
        }
    }
    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return 1;
    }
    @Override
    public int getItemCount() {
        return dataList.size();
    }
    //监听事件的回调方法接口
    private OnItemClickListener mOnItemClickListener = null;
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }
    class listHolder extends RecyclerView.ViewHolder{
        TextView recordAccount;
        TextView recordCreateTime;
        TextView recordContent;
        FlowTagLayout showTagLayout;
        public listHolder(View itemView) {
            super(itemView);
            showTagLayout = (FlowTagLayout) itemView.findViewById(R.id.record_tag_list);
            recordAccount = (TextView)itemView.findViewById(R.id.record_account);
            recordCreateTime = (TextView)itemView.findViewById(R.id.record_create_time);
            recordContent = (TextView)itemView.findViewById(R.id.record_content);
        }
    }
}
