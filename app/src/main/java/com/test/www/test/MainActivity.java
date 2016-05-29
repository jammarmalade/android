package com.test.www.test;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹框显示信息
//                Toast.makeText(MainActivity.this,"you clicked btn_ok ",Toast.LENGTH_SHORT).show();
                //启动第二个活动
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                startActivity(intent);
                //隐式调用
//                Intent intent = new Intent("com.test.mainactivity.ACTION_START");
//                //可添加多个 category
//                intent.addCategory("com.test.mainactivity.MY_CATEGORY");
//                startActivity(intent);
                //调用其他程序的 活动
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://www.baidu.com/"));
//                startActivity(intent);
                //向下一个活动传递数据
//                String data = "Hello Jam";
//                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
//                intent.putExtra("extra_data",data);
//                startActivity(intent);
                //返回启动活动销毁后返回的数据
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                //请求码，用于在回调中判断数据来源
                //返回后会调用 onActivityResult() 方法
                startActivityForResult(intent,1);

            }
        });

    }
    /**
     * 重写菜单
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    /**
     * 菜单响应事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add_item:
                Toast.makeText(this, "You clicked Add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(this, "You clicked Remove", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        //销毁一个活动
//        finish();
        return true;
    }

    /**
     * 用于获取下一个活动返回的数据
     * @param requestCode   启动活动时传入的请求码
     * @param resultCode    返回数据时传入的处理结果
     * @param data          携带返回数据的 Intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    String returnData = data.getStringExtra("data_return");
                    Log.d("MainActivity",returnData);
                }
                break;
            default:
        }
    }


}
