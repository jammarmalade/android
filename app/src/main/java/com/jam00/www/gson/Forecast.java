package com.jam00.www.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weijingtong20 on 2016/12/26.
 */

public class Forecast {
    public String date;

    @SerializedName("tmp")
    public Temperature temperature;
    public class Temperature{
        public String max;
        public String min;
    }

    @SerializedName("cond")
    public More more;
    public class More{
        @SerializedName("txt_d")
        public String info;
    }
}
