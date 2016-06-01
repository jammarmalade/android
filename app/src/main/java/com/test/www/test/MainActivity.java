package com.test.www.test;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    ListView contactsView;
    ArrayAdapter<String> adapter;
    List<String> contactsList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        contactsView = (ListView) findViewById(R.id.contacts_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList);
        contactsView.setAdapter(adapter);
        readContacts();
    }

    private void readContacts() {
        Cursor cursor = null;
        try {
            // 查询联系人数据 ContactsContract.CommonDataKinds.Phone.CONTENT_URI 就是 Uri.parse()方法解析出来的结果
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, null, null, null);
            while (cursor.moveToNext()) {
                // 获取联系人姓名 DISPLAY_NAME 用户名常量
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                // 获取联系人手机号 NUMBER 手机号码常量
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contactsList.add(displayName + "\n" + number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
