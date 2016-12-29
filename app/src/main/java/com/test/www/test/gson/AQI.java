package com.test.www.test.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weijingtong20 on 2016/12/26.
 */

public class AQI {
    public AQICity city;
    public class AQICity{
        public String aqi;
        public String pm25;
    }
}
