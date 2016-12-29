package com.test.www.test.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weijingtong20 on 2016/12/26.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("cw")
    public CarWash carWash;
    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public Sport sport;
    public class Sport{
        @SerializedName("txt")
        public String info;
    }
}
