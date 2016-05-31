package com.test.www.test;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends BaseActivity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.button:
                        //创建待添加的碎片实例
                        AnotherRightFragment fragment = new AnotherRightFragment();
                        //获取到 FragmentManager，在活动中可以直接调用 getFragmentManager()方法得到
                        FragmentManager fragmentManager = getFragmentManager();
                        //开启一个事务，通过调用 beginTransaction()方法开启
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        //向容器内加入碎片，一般使用 replace()方法实现，需要传入容器的 id 和待添加的碎片实例
                        transaction.replace(R.id.right_layout, fragment);
                        //将一个事务添加到返回栈中(按返回键就不会直接退出程序)
                        transaction.addToBackStack(null);
                        //提交事务，调用 commit()方法来完成
                        transaction.commit();
                        break;
                    default:
                        break;
                }
            }
        });
        //活动中得到相应碎片的实例，然后就能轻松地调用碎片里的方法了
        RightFragment rightFragment = (RightFragment) getFragmentManager().findFragmentById(R.id.right_fragment);
    }

}
