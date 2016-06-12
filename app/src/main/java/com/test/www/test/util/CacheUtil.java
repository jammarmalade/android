package com.test.www.test.util;

import android.content.Context;
import android.os.Environment;

import com.test.www.test.activity.BaseActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/11.
 * 文件缓存类
 */
public class CacheUtil {

    private static File CacheRoot;

    /**
     * 存储Json文件
     *
     * @param
     * @param json     json字符串
     * @param fileName 存储的文件名
     * @param append   true 增加到文件末，false则覆盖掉原来的文件
     */
    public static void writeJson(Context c, String json, String fileName, boolean append) {
        CacheRoot = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ? c.getExternalCacheDir() : c.getCacheDir();
        FileOutputStream fos = null;
        ObjectOutputStream os = null;
        try {
            File ff = new File(CacheRoot, fileName);
            boolean boo = ff.exists();
            fos = new FileOutputStream(ff, append);
            os = new ObjectOutputStream(fos);
            if (append && boo) {
                FileChannel fc = fos.getChannel();
                fc.truncate(fc.position() - 4);
            }
            os.writeObject(json);
            ff.setLastModified(System.currentTimeMillis());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取json数据
     *
     * @param c
     * @param fileName
     * @param expire    过期时间（秒）
     * @return 返回值为list
     */
    @SuppressWarnings("resource")
    public static List<String> readJson(Context c, String fileName,int expire) {
        CacheRoot = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ? c.getExternalCacheDir() : c.getCacheDir();
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        List<String> result = new ArrayList<String>();
        File des = new File(CacheRoot, fileName);
        //文件不存在就直接返回
        if(!des.exists()){
            return result;
        }
        //是否过了缓存期
        if((System.currentTimeMillis() - des.lastModified()) > (expire * 1000)){
            return result;
        }
        try {
            fis = new FileInputStream(des);
            ois = new ObjectInputStream(fis);
            while (fis.available() > 0)
                result.add((String) ois.readObject());
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String getCacheDir(Context c){
        CacheRoot = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ? c.getExternalCacheDir() : c.getCacheDir();
        return CacheRoot.getPath();
    }
    /**
     * 删除目录及目录下的所有文件
     * @param sPath
     * @return
     */
    public static boolean deleteDirectory(String sPath) {
        // 如果sPath不以文件分隔符结尾，自动添加文件分隔符
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        Boolean flag = true;
        // 删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            } // 删除子目录
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag)
            return false;
        // 删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除单个文件
     * @param sPath     路径
     * @return
     */
    public static boolean deleteFile(String sPath) {
        Boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
}
