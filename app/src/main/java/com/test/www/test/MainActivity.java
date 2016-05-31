package com.test.www.test;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    private ListView msgListView;
    private EditText inputText;
    private Button send;
    private MsgAdapter adapter;
    private List<Msg> msgList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        //初始化消息数据
        initMsgs();
        adapter = new MsgAdapter(MainActivity.this , R.layout.msg_item , msgList);
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button)findViewById(R.id.send);
        msgListView = (ListView)findViewById(R.id.msg_list_view);
        msgListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputText.getText().toString();
                //若是内容不为空
                if(!"".equals(content)){
                    Msg msg = new Msg(content , Msg.TYPE_SENT);
                    msgList.add(msg);
                    //当有新消息时刷新 ListView 中的显示
                    adapter.notifyDataSetChanged();
                    //将 ListView 定位到最后一行
                    msgListView.setSelection(msgList.size());
                    //清空输入框
                    inputText.setText("");
                }
            }
        });
    }
    private void initMsgs(){
        Msg msg1 = new Msg("Hello Jam.",Msg.TYPE_RECEIVED);
        msgList.add(msg1);
        Msg msg2 = new Msg("Hello . who is that?.",Msg.TYPE_SENT);
        msgList.add(msg2);
        Msg msg3 = new Msg("This is Tom . Nice talking to you.",Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }

}
