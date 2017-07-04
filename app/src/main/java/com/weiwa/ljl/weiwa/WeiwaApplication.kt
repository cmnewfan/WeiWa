package com.weiwa.ljl.weiwa

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment

import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.microedition.khronos.opengles.GL11

/**
 * Created by hzfd on 2017/2/7.
 */
class WeiwaApplication : Application() {

    fun create() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, e ->
            e.printStackTrace() // not all Android versions will print the stack trace automatically
            System.exit(1)
        }
    }

    companion object {
        var CacheCategory = File(Environment.getExternalStorageDirectory().absolutePath + "/Weiwa")
        var CachePortrait = File(CacheCategory.absolutePath + "/Portrait")

        fun getFileName(uri: Uri): String {
            var uri_string = uri.toString()
            uri_string = uri_string.substring(uri_string.lastIndexOf("/") + 1, uri_string.lastIndexOf("."))
            return uri_string
        }

        fun getFileName(url: URL): String {
            var uri_string = url.toString()
            uri_string = uri_string.substring(uri_string.lastIndexOf("/") + 1, uri_string.lastIndexOf("."))
            return uri_string
        }
    }
}

class BitmapDownloadHelper {
    companion object {
        val IMAGE = 0
        val PORTRAIT = 1

        fun getCachedImage(name: String, downloadType: Int): File? {
            var targetCategory: File? = null
            when (downloadType) {
                PORTRAIT -> targetCategory = WeiwaApplication.CachePortrait
                IMAGE -> targetCategory = WeiwaApplication.CacheCategory
            }
            if (targetCategory!!.exists()) {
                for (file in targetCategory.listFiles()) {
                    val fileName = file.name
                    if (fileName == name) {
                        return file
                    }
                }
                return null
            } else {
                return null
            }
        }

        fun downloadFileFromURL(url: URL, fileName: String, downloadType: Int): File {
            //获得连接
            val conn = url.openConnection() as HttpURLConnection
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.connectTimeout = 6000
            //连接设置获得数据流
            conn.doInput = true
            //不使用缓存
            conn.useCaches = false
            //得到数据流
            val downloadFile: File =
                    if (downloadType == PORTRAIT) File(WeiwaApplication.CachePortrait, WeiwaApplication.getFileName(url))
                    else File(WeiwaApplication.CacheCategory, WeiwaApplication.getFileName(url))
            val `is` = conn.inputStream
            val os = FileOutputStream(downloadFile)
            var bytesRead = 0
            val buffer = ByteArray(8192)
            while (true) {
                bytesRead = `is`.read(buffer, 0, 8192)
                if (bytesRead != -1) {
                    os.write(buffer, 0, bytesRead)
                } else {
                    break
                }
            }
            os.close()
            `is`.close()
            return downloadFile
        }

        fun adjustBitmap(result: Bitmap): Bitmap {
            var result = result
            if (result.height > GL11.GL_MAX_TEXTURE_SIZE) {
                val newHeight = GL11.GL_MAX_TEXTURE_SIZE
                val newWidth = (result.width.toFloat() / result.height.toFloat() * newHeight).toInt()
                result = Bitmap.createBitmap(result, 0, 0, newWidth, newHeight)
            }
            return result
        }
    }
}