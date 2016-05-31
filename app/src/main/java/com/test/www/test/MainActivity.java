package com.test.www.test;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {
    static final String TAG = "MainActivity";
    private List<Fruit> fruitList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //初始化水果数据
        initFruits();
        Log.d(TAG,fruitList.toString());
        //使用自己的适配器，为每一行数据添加布局
        FruitAdapter adapter = new FruitAdapter(MainActivity.this , R.layout.fruit_item,fruitList);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        //设置 ListView 响应点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取被点击的那个 item 实例
                Fruit fruit = fruitList.get(position);
                Toast.makeText(MainActivity.this, fruit.getName(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void initFruits(){
        String[] data = {"Apple","Banana","Orange","Watermelon","Pear","Grape","Pineapple","Strawberry","Cherry","Mango","test1",
                "test2","test3","test4","test5","test6","test7","test8","test9"};
        String fruit;
        int fruitImage;
        for(String fruitName : data){
            fruit = fruitName.toLowerCase();
            //资源id
            if(fruit.substring(0,4).equals("test")){
                //使用默认图
                fruitImage = R.mipmap.ic_launcher;
            }else{
                //动态获取图片资源 ,文件名称，文件夹，包名（项目包名称 com.test.www.test）
                fruitImage = getResources().getIdentifier(fruit, "drawable", getPackageName());
            }
            fruitList.add(new Fruit(fruitName,fruitImage));
        }

    }
}
