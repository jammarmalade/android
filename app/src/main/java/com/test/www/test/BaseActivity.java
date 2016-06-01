package com.test.www.test;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by weijingtong20 on 2016/6/1.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle sis){
        super.onCreate(sis);
        ActivityCollector.addActivity(this);
    }
    protected void onDestroy(){
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
