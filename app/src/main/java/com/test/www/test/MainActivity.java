package com.test.www.test;


import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private TextView positionTextView;
    private TextView serverTextView;
    public static final int SHOW_LOCATION = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_LOCATION:
                    String currentPosition = (String) msg.obj;
                    positionTextView.setText(currentPosition);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        positionTextView = (TextView)findViewById(R.id.position_text_view);
        //32.7763644055,100.4338731743
        showLocation(32.7763644055,100.4338731743);
    }

    private void showLocation(final double latitude,final double longitude) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //  组装反向地理编码的接口地址
                    StringBuilder url = new StringBuilder();
                    url.append("http://maps.google.cn/maps/api/geocode/json?language=CN&latlng=");
//                    url.append("http://192.168.1.46/xx.php?language=CN&latlng=");
                    url.append(latitude).append(",");
                    url.append(longitude);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url.toString());
                    //  在请求消息头中指定语言，保证服务器会返回中文数据
                    httpGet.addHeader("Accept-Language", "zh-CN");
                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = httpResponse.getEntity();
                        String response = EntityUtils.toString(entity, "utf-8");
                        JSONObject jsonObject = new JSONObject(response);
                        //  获取results 节点下的位置信息
                        JSONArray resultArray = jsonObject.getJSONArray("results");
                        if (resultArray.length() > 0) {
                            JSONObject subObject = resultArray.getJSONObject(1);
                            //  取出格式化后的位置信息
                            String address = subObject.getString("formatted_address");
                            Message message = new Message();
                            message.what = SHOW_LOCATION;
                            message.obj = address;
                            handler.sendMessage(message);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


}
