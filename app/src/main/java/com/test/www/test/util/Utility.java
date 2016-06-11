package com.test.www.test.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.test.www.test.model.Area;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Administrator on 2016/6/10.
 * 用于获取 省市县/区
 */
public class Utility {
    public static final String TAG = "Utility";
    public static boolean handleArea(String jsonData , String type){


        Gson gson = new Gson();
        List<Area> areaList = gson.fromJson(jsonData, new TypeToken<List<Area>>() {}.getType());
        for (Area area : areaList) {
            LogUtil.d(TAG, area.getId()+" - "+area.getName()+" - "+area.getCode());
        }
        return true;
    }


}
