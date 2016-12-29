package com.jam00.www.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weijingtong20 on 2016/12/26.
 */

public class Basic {
    //建立映射（json 中的字段不适合直接作为java字段来命名）
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
