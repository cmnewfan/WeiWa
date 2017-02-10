package com.weiwa.ljl.weiwa.Helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzfd on 2017/1/18.
 */
public class Helper extends AsyncTask{

    @Override
    protected Object doInBackground(Object[] params) {
        String[] urls = (String[]) params[0];
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int i=0;i<urls.length;i++){
            try{
                URL myFileURL = new URL(urls[i]);
                //获得连接
                HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                conn.setConnectTimeout(6000);
                //连接设置获得数据流
                conn.setDoInput(true);
                //不使用缓存
                conn.setUseCaches(false);
                //这句可有可无，没有影响
                //conn.connect();
                //得到数据流
                InputStream is = conn.getInputStream();
                //解析得到图片
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                //关闭数据流
                is.close();
                bitmaps.add(bitmap);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return bitmaps;
    }

}
