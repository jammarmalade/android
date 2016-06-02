package com.test.www.test;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends Activity {
    public static final String TAG = "MainActivity";
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    private Button takePhoto;
    private ImageView picture;
    private Uri imageUri;
    private Button chooseFromAlbum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        //拍照
        takePhoto = (Button) findViewById(R.id.take_photo);
        picture = (ImageView) findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建File对象，用于存储拍照后的图片
                //Environment.getExternalStorageDirectory() 获取到的就是手机 SD 卡的根目录
                File outputImage = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");

                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //将 File 对象转换成 Uri 对象
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                //指定图片的输出地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                //隐式  Intent,拍下的照片将会输出到 output_image.jpg 中
                startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
            }
        });

        //从相册中选择
        chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUri = Uri.parse("file://"+Environment.getExternalStorageDirectory()+"/tempImage.jpg");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT,null);
                intent.setType("image/*");
                intent.putExtra("crop", "true");//可裁剪
                intent.putExtra("aspectX", 2);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", 600);
                intent.putExtra("outputY", 300);
                intent.putExtra("scale", true);//可缩放
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CROP_PHOTO);

            }
        });
    }

    /**
     * 照相完成后调用
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //照相成功，构建一个 Intent 对照片进行裁剪
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    //裁剪成功
                    try {
                        Log.d(TAG,"130");
                        //调用 BitmapFactory 的 decodeStream()方法将 output_image.jpg 这张照片解析成 Bitmap 对象
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                        picture.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                        picture.setVisibility(View.VISIBLE);
                    } catch (FileNotFoundException e) {
                        Log.d(TAG,"136");
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

}
