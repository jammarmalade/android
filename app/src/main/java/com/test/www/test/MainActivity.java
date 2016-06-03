package com.test.www.test;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;


public class MainActivity extends Activity implements View.OnClickListener{
    public static final String TAG = "MainActivity";
    public static final int UPDATE_TEXT = 1;
    private TextView text;
    private Button changeText;
    private Button asynctask;
    private ProgressDialog progressDialog;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_TEXT:
                    //  在这里可以进行UI 操作
                    text.setText("Nice to meet you");
                    break;
                default:
                    break;
            }
        }
    };
    private LinkedList<Integer> intArr = new LinkedList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.text);
        changeText = (Button) findViewById(R.id.change_text);
        changeText.setOnClickListener(this);

        //asynctask 测试
        asynctask = (Button)findViewById(R.id.asynctask);
        // 弹出要给ProgressDialog
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("提示信息");
        progressDialog.setMessage("正在下载中，请稍后......");
        //    设置setCancelable(false); 表示我们不能取消这个弹出框，等下载完成之后再让弹出框消失
        progressDialog.setCancelable(false);
        //    设置ProgressDialog样式为圆圈的形式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asynctask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 在UI Thread当中实例化AsyncTask对象，并调用execute方法
                new DownloadTask().execute();
            }
        });
        //模拟下载数据
        for(int i=1 ; i <= 100 ;i++){
            intArr.add(i);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_text:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = UPDATE_TEXT;
                        handler.sendMessage(message); //  将Message 对象发送出去
                    }
                }).start();
                break;
            default:
                break;
        }
    }
    private int doDownload(){
        int tmp = intArr.removeFirst();
        try {
            Thread.currentThread().sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmp;
    }
    /**
     *  AsyncTask 测试
     */
    class DownloadTask extends AsyncTask<Void, Integer ,Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog.show(); // 显示进度对话框
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                while (true) {
                    int downloadPercent = doDownload(); // 这是一个虚构的方法,返回下载进度
                    // 调用 publishProgress ，onProgressUpdate() 方法会被执行
                    publishProgress(downloadPercent);
                    if (downloadPercent >= 100) {
                        break;
                    }
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            // 在这里更新下载进度
            progressDialog.setMessage("Downloaded " + values[0] + "%");
        }
        @Override
        protected void onPostExecute(Boolean result) {
            progressDialog.dismiss(); // 关闭进度对话框
            // 在这里提示下载结果
            if (result) {
                Toast.makeText(MainActivity.this, "Download succeeded", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, " Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
