package com.test.www.test;


import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        dbHelper = new MyDatabaseHelper(this, "BookStore.db", null, 1);
        //----创建数据库
        Button createDatabase = (Button) findViewById(R.id.create_database);
        createDatabase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.getWritableDatabase();
            }
        });
        //----添加数据
        Button addData = (Button) findViewById(R.id.add_data);
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取 SQLiteDatabase 对象
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //使用 ContentValues 来对要添加的数据进行组装
                ContentValues values = new ContentValues();
                //  开始组装第一条数据
                values.put("name", "The Da Vinci Code");
                values.put("author", "Dan Brown");
                values.put("pages", 454);
                values.put("price", 16.96);
                // 表名称， 在未指定添加数据的情况下给某些可为空的列自动赋值 NULL ，插入数据
                db.insert("Book", null, values); //  插入第一条数据
                values.clear();
                //  开始组装第二条数据
                values.put("name", "The Lost Symbol");
                values.put("author", "Dan Brown");
                values.put("pages", 510);
                values.put("price", 19.95);
                db.insert("Book", null, values); //  插入第二条数据
                Toast.makeText(MainActivity.this, "Add succeeded", Toast.LENGTH_SHORT).show();
            }
        });
        //----更新数据
        Button updateData = (Button) findViewById(R.id.update_data);
        updateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取 SQLiteDatabase 对象
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //使用 ContentValues 来对要更新的数据进行组装
                ContentValues values = new ContentValues();
                values.put("price", 10.99);
                //表名称， 要更新的数据，更新条件(占位符)，具体要更新的条件
                db.update("Book", values, "name = ?", new String[] { "The Da Vinci Code" });
                Toast.makeText(MainActivity.this, "Update succeeded", Toast.LENGTH_SHORT).show();
            }
        });

        //----删除数据
        Button deleteButton = (Button) findViewById(R.id.delete_data);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //表名称， 更新条件(占位符)，具体要更新的条件
                db.delete("Book", "pages > ?", new String[] { "500" });
                Toast.makeText(MainActivity.this, "Delete succeeded", Toast.LENGTH_SHORT).show();
            }
        });

        //----查询数据
        Button queryButton = (Button) findViewById(R.id.query_data);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                //  查询Book 表中所有的数据
                Cursor cursor = db.query("Book", null, null, null, null, null, null);
                //将数据的指针移动到第一行的位置
                if (cursor.moveToFirst()) {
                    //  遍历Cursor 对象，取出数据并打印
                    do {
                        // getColumnIndex()方法获取到某一列在表中对应的位置索引,然后将这个索引传入到相应的取值方法中
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));
                        Log.d(TAG, "book name is " + name);
                        Log.d(TAG, "book author is " + author);
                        Log.d(TAG, "book pages is " + pages);
                        Log.d(TAG, "book price is " + price);
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        });

        //----使用事务
        Button replaceData = (Button) findViewById(R.id.replace_data);
        replaceData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.beginTransaction(); //  开启事务
                try {
                    //删除 book 表的数据
                    db.delete("Book", null, null);
                    if (true) {
                        //  在这里手动抛出一个异常，让事务失败
                        throw new NullPointerException();
                    }
                    ContentValues values = new ContentValues();
                    values.put("name", "Game of Thrones");
                    values.put("author", "George Martin");
                    values.put("pages", 720);
                    values.put("price", 20.85);
                    db.insert("Book", null, values);
                    db.setTransactionSuccessful(); //  事务已经执行成功
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.endTransaction(); //  结束事务
                }
            }
        });
    }


}
