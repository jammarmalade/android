package com.jam00.www.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by weijingtong20 on 2017/7/20.
 */

public class Record {
    public String status;
    public String message;
    
    @SerializedName("result")
    public List<info> recordList;

    public static class info{
        public String id;
        public String uid;
        public String username;
        public int type;
        public double account;
        public String content;
        public String longitude;
        public String latitude;
        public int status;
        public String date;
        public String showTime;
        public List<TagInfo> tagList;

    }
}
