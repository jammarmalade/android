package com.jam00.www.db;

import org.litepal.crud.DataSupport;

/**
 * Created by weijingtong20 on 2016/12/26.
 * 省
 */
public class Provice extends DataSupport{
    private int id;
    private String proviceName;
    private int proviceCode;
    private String weatherId;
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
    public String getProviceName(){
        return proviceName;
    }
    public void setProviceName(String proviceName){
        this.proviceName = proviceName;
    }
    public int getProviceCode(){
        return proviceCode;
    }
    public void setProviceCode(int proviceCode){
        this.proviceCode = proviceCode;
    }
    public String getWeatherId(){
        return weatherId;
    }
    public void setWeatherId(String weatherId){
        this.weatherId = weatherId;
    }
}
