package com.jam00.www.gson;

/**
 * Created by weijingtong20 on 2018/1/18.
 * 弹出菜单的单个信息
 */

public class MenuInfo {
    private int id;
    private int icon;
    private String title;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public int getIcon(){
        return icon;
    }
    public void setIcon(int icon){
        this.icon = icon;
    }
    public String getTitle(){
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
}
