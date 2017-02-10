package com.weiwa.ljl.weiwa;

import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.net.URL;

/**
 * Created by hzfd on 2017/2/7.
 */
public class WeatherApplication {
    public static File CacheCategory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Weiwa");

    public static String getFileName(Uri uri){
        String uri_string = uri.toString();
        uri_string = uri_string.substring(uri_string.lastIndexOf("/")+1,uri_string.lastIndexOf("."));
        return uri_string;
    }

    public static String getFileName(URL url){
        String uri_string = url.toString();
        uri_string = uri_string.substring(uri_string.lastIndexOf("/")+1,uri_string.lastIndexOf("."));
        return uri_string;
    }

}
