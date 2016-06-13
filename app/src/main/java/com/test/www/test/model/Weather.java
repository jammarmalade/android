package com.test.www.test.model;

/**
 * Created by weijingtong20 on 2016/6/13.
 * 未来几天天气
 */
public class Weather {
    private String tmpHigh;
    private String tmpLow;
    private String type;
    private String date;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getTmpHigh() {
        return tmpHigh;
    }
    public void setTmpHigh(String tmpHigh) {
        this.tmpHigh = tmpHigh;
    }
    public String getTmpLow() {
        return tmpLow;
    }
    public void setTmpLow(String tmpLow) {
        this.tmpLow = tmpLow;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
