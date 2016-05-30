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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    private EditText editText;
    private ImageView imageView;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        editText = (EditText)findViewById(R.id.edit_text);
        imageView = (ImageView)findViewById(R.id.image_view);
        Button button = (Button)findViewById(R.id.button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button:
                        //弹出输入框输入的值
//                        String inputString = editText.getText().toString();
//                        Toast.makeText(MainActivity.this,inputString,Toast.LENGTH_SHORT).show();
                        //动态修改imageView 的值
//                        imageView.setImageResource(R.drawable.t_wb);
                        //设置进度条的可见性
//                        if(progressBar.getVisibility() == View.GONE){
//                            progressBar.setVisibility(View.VISIBLE);
//                        }else{
//                            progressBar.setVisibility(View.GONE);
//                        }
                        //设置进度条的进度
//                        int progress = progressBar.getProgress();
//                        progress = progress + 10;
//                        progressBar.setProgress(progress);
                        //弹出警告框（屏蔽其他交互功能）
//                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
//                        dialog.setTitle("This is Dialog");
//                        dialog.setMessage("Please type your phone number");
//                        dialog.setCancelable(false);
//                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //点击 ok 后执行
//                            }
//                        });
//                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //点击 Cancel 后执行
//                            }
//                        });
//                        dialog.show();
                        //进度条对话框
                        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setTitle("This is ProgressDialog");
                        progressDialog.setMessage("Loading");
                        progressDialog.setCancelable(true);
                        progressDialog.show();
                        
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
