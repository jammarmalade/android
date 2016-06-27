package com.test.www.test;


import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";

    private Button mButton;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mButton = (Button) findViewById(R.id.button);
        mImageView= (ImageView) findViewById(R.id.image);


        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = "http://www.weijingtong.com/upload/article/20160504/1462332392_543689.jpg_640x3200.jpg";
                ImageCacheManager.loadImage(url, mImageView, getBitmapFromRes(R.mipmap.ic_launcher), getBitmapFromRes(R.mipmap.ic_launcher));
            }
        });
    }

    public Bitmap getBitmapFromRes(int resId) {
        Resources res = this.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }


}
