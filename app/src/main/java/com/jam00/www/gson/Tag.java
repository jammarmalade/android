package com.jam00.www.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by weijingtong20 on 2017/7/20.
 */

public class Tag {
    public String status;
    public String message;
    
    @SerializedName("result")
    public List<info> tagList;

    public static class info{
        public String id;
        public String name;
        public String img;
    }
}
