package com.jam00.www.custom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jam00.www.R;
import com.jam00.www.activity.BaseActivity;
import com.jam00.www.activity.LoginActivity;
import com.jam00.www.gson.MenuInfo;
import com.jam00.www.util.LogUtil;

import java.util.List;

import android.widget.PopupWindow;

/**
 * Created by weijingtong20 on 2018/1/18.
 * 弹出菜单
 */

public class MenuPopwindow extends PopupWindow {

    private View contentView;
    private ListView lvContent;
    private LinearLayout linearLayout;

    public MenuPopwindow(Activity context,List<MenuInfo> list){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.menu_popup_window,null);
        lvContent = (ListView) contentView.findViewById(R.id.lv_toptitle_menu);
        lvContent.setAdapter(new MyAdapter(context, list));

        //getHeight 和 getWidth 已被丢弃
//        int h = context.getWindowManager().getDefaultDisplay().getHeight();
//        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int h = dm.heightPixels;
        int w = dm.widthPixels;

        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w / 2);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupAnimation);
    }

    public void setOnItemClick(AdapterView.OnItemClickListener myOnItemClickListener) {
        lvContent.setOnItemClickListener(myOnItemClickListener);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent,0,35);
        } else {
            this.dismiss();
        }
    }

    //适配器
    class MyAdapter extends BaseAdapter{
        private List<MenuInfo> list;
        private LayoutInflater inflater;

        public MyAdapter(Context context,List<MenuInfo> list){
            inflater = LayoutInflater.from(context);
            this.list = list;
        }
        @Override
        public int getCount(){
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position){
            return list.get(position);
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Holder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.menu_popup_window_item, null);
                holder = new Holder();
                holder.ivItem = (ImageView) convertView.findViewById(R.id.iv_menu_item);
                holder.tvItem = (TextView) convertView.findViewById(R.id.tv_menu_item);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            holder.ivItem.setImageResource(list.get(position).getIcon());
            holder.tvItem.setText(list.get(position).getTitle());
            return convertView;
        }

        class Holder {
            ImageView ivItem;
            TextView tvItem;
        }
    }
}
