package com.weiwa.ljl.weiwa.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.ImageView
import com.weiwa.ljl.weiwa.BitmapDownloadHelper
import com.weiwa.ljl.weiwa.WeiwaApplication
import com.weiwa.ljl.weiwa.activity.MainActivity
import com.weiwa.ljl.weiwa.network.WeiboPojo
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.net.URL
import javax.microedition.khronos.opengles.GL11

/**
 * Created by hzfd on 2017/2/13.
 */
class PortraitView : ImageView {
    private var mUser: WeiboPojo.User? = null
    private var instance: MainActivity? = null

    constructor(context: Context, user: WeiboPojo.User) : super(context) {
        instance = context as MainActivity
        mUser = user
    }

    constructor(context: Context, downloadUrl: String, downloadCount: Int, downloadType: Int) : super(context) {
        instance = context as MainActivity
        addDownloadTask(downloadUrl, downloadCount, downloadType)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    fun addDownloadTask(downloadUrl: String, downloadCount: Int, downloadType: Int) {
        async(CommonPool) {
            var myFileURL: URL? = null
            var isGif: Boolean? = null
            try {
                myFileURL = URL(downloadUrl)
                if (!downloadUrl.toLowerCase().endsWith(".gif")) {
                    myFileURL = URL(downloadUrl.replace("thumbnail", "bmiddle"))
                    isGif = false
                }
                //if image cached
                val cachedFile = BitmapDownloadHelper.getCachedImage(WeiwaApplication.getFileName(myFileURL), downloadType)
                if (cachedFile != null) {
                    val bitmap = BitmapDownloadHelper.adjustBitmap(BitmapFactory.decodeFile(cachedFile.absolutePath))
                    instance!!.runOnUiThread {
                        setImage(bitmap, downloadType, downloadCount)
                    }
                } else {
                    val bitmap = BitmapDownloadHelper.adjustBitmap(BitmapFactory.decodeFile(BitmapDownloadHelper.downloadFileFromURL(myFileURL, WeiwaApplication.getFileName(myFileURL), downloadType).absolutePath))
                    instance!!.runOnUiThread {
                        setImage(bitmap, downloadType, downloadCount)
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun setImage(result: Bitmap, downloadType: Int, count: Int) {
        val divider = 100
        if (downloadType == BitmapDownloadHelper.IMAGE) {
            if (count == 1) {
                layoutParams.width = resources.displayMetrics.widthPixels - divider
                if (result.height > GL11.GL_MAX_TEXTURE_SIZE) {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                } else {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            } else if (count == 2) {
                layoutParams.width = (resources.displayMetrics.widthPixels - divider) / 2
                scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                if (layoutParams == null) {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
                layoutParams.width = (resources.displayMetrics.widthPixels - divider) / 3
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            setImageBitmap(result)
        } else if (downloadType == BitmapDownloadHelper.PORTRAIT) {
            setImageBitmap(result)
        }
    }

    fun setData(context: MainActivity, user: WeiboPojo.User) {
        instance = context
        mUser = user
        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserViewFragment userViewFragment = new UserViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", mUser.getScreen_name());
                bundle.putString("decription",mUser.getDescription());
                bundle.putString("location",mUser.getLocation());
                bundle.putString("gender",mUser.getGender());
                bundle.putString("follow_me",mUser.getFollow_me());
                bundle.putString("uid",mUser.getId());
                userViewFragment.setArguments(bundle);
                instance.setFragment(userViewFragment);
            }
        });*/
    }
}
