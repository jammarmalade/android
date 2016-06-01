package com.test.www.test;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
    }

}
