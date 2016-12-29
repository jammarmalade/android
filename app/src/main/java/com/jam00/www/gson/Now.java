package com.jam00.www.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weijingtong20 on 2016/12/26.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
