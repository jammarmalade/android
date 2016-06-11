package com.test.www.test.model;

/**
 * Created by Administrator on 2016/6/10.
 * 获取省市县/区域 的实体类
 */
public class Area {
    private int id;
    private String name;
    private String code;
    private String alias;
    private int upid;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public int getUpid() {
        return upid;
    }
    public void setUpid(int upid) {
        this.upid = upid;
    }
}
