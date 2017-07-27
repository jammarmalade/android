package com.jam00.www.gson;


import com.google.gson.annotations.SerializedName;

/**
 * Created by weijingtong20 on 2017/7/20.
 */

public class Login {
    public String status;
    public String message;
    @SerializedName("result")
    public UserInfo userInfo;

    public class UserInfo {
        public String id;
        public String username;
        public String email;
        public String head;
        public String authkey;
        public String des;
    }
}
