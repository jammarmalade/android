package com.test.www.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by weijingtong20 on 2016/6/1.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    //Book 表
    public static final String CREATE_BOOK = "create table Book ("
            + "id integer primary key autoincrement, "
            + "author text, "
            + "price real, "
            + "pages integer, "
            + "name text)";
    //Category 表
    public static final String CREATE_CATEGORY = "create table Category ("
            + "id integer primary key autoincrement, "
            + "category_name text, "
            + "category_code integer)";
    private Context mContext;

    /**
     *
     * @param context
     * @param name      数据库名称
     * @param factory
     * @param version   版本号，传入比上次版本号高的数字就会执行 onUpgrade
     */
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        db.execSQL(CREATE_CATEGORY);
        //Sql 操作数据库
        /*
        //添加
        db.execSQL("insert into Book (name, author, pages, price) values(?, ?, ?, ?)",
                new String[] { "The Da Vinci Code", "Dan Brown", "454", "16.96" });
        db.execSQL("insert into Book (name, author, pages, price) values(?, ?, ?, ?)",
                new String[] { "The Lost Symbol", "Dan Brown", "510", "19.95" });
        //更新数据
        db.execSQL("update Book set price = ? where name = ?", new String[] { "10.99",
                "The Da Vinci Code" });
        //删除数据
        db.execSQL("delete from Book where pages > ?", new String[] { "500" });
        //查询数据
        db.rawQuery("select * from Book", null);
        */
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }

    /**
     * 实例化时，传入版本号大于上一次的版本号就会执行
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists Book");
        db.execSQL("drop table if exists Category");
        onCreate(db);
    }
}
