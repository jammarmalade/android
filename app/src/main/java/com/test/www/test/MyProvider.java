package com.test.www.test;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Administrator on 2016/6/1.
 * 自定义的内容提供器
 */
public class MyProvider  extends ContentProvider{

    public static final int TABLE1_DIR = 0;
    public static final int TABLE1_ITEM = 1;
    public static final int TABLE2_DIR = 2;
    public static final int TABLE2_ITEM = 3;
    private static UriMatcher uriMatcher;
    static {
        /**
         1. *：表示匹配任意长度的任意字符
         2. #：表示匹配任意长度的数字
         所以，一个能够匹配任意表的内容 URI 格式就可以写成：
         content://com.example.app.provider/*
         而一个能够匹配 table1 表中任意一行数据的内容 URI 格式就可以写成：
         content://com.example.app.provider/table1/#
         */
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.test.app.provider", "table1", TABLE1_DIR);
        uriMatcher.addURI("com.test.app.provider ", "table1/#", TABLE1_ITEM);
        uriMatcher.addURI("com.test.app.provider ", "table2", TABLE2_ITEM);
        uriMatcher.addURI("com.test.app.provider ", "table2/#", TABLE2_ITEM);
    }

    /**
     * 初始化内容提供器的时候调用 .只有当存在ContentResolver 尝试访问我们程序中的数据时，内容提供器才会被初始化
     * @return
     */
    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     * 从内容提供器中查询数据
     * @param uri
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case TABLE1_DIR:
                //  查询table1 表中的所有数据
                break;
            case TABLE1_ITEM:
                //  查询table1 表中的单条数据
                break;
            case TABLE2_DIR:
                //  查询table2 表中的所有数据
                break;
            case TABLE2_ITEM:
                //  查询table2 表中的单条数据
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * 向内容提供器中添加一条数据
     * @param uri
     * @param values
     * @return
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    /**
     * 更新内容提供器中已有的数据
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * 从内容提供器中删除数据
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    /**
     * 根据传入的内容 URI 来返回相应的 MIME 类型
     * 一个内容 URI 所对应的 MIME字符串主要由三部分组分
     1. 必须以 vnd 开头。
     2. 如果内容 URI 以路径结尾，则后接 android.cursor.dir/，如果内容 URI 以 id 结尾，则后接 android.cursor.item/。
     3. 最后接上 vnd.<authority>.<path>。
     对于 content://com.example.app.provider/table1 这个内容 URI，它所对应的 MIME类型就可以写成
        vnd.android.cursor.dir/vnd.com.example.app.provider.table1
     对于 content://com.example.app.provider/table1/1 这个内容 URI，它所对应的 MIME 类型就可以写成：
        vnd.android.cursor.item/vnd. com.example.app.provider.table1
     * @param uri
     * @return
     */
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TABLE1_DIR:
                return "vnd.android.cursor.dir/vnd.com.test.app.provider.table1";
            case TABLE1_ITEM:
                return "vnd.android.cursor.item/vnd.com.test.app.provider.table1";
            case TABLE2_DIR:
                return "vnd.android.cursor.dir/vnd.com.test.app.provider.table2";
            case TABLE2_ITEM:
                return "vnd.android.cursor.item/vnd.com.test.app.provider.table2";
            default:
                break;
        }
        return null;
    }
}
