package com.weiwa.ljl.weiwa.view

import android.content.Context
import android.graphics.BitmapFactory
import android.util.AttributeSet
import com.weiwa.ljl.weiwa.BitmapDownloadHelper
import com.weiwa.ljl.weiwa.WeiwaApplication
import com.weiwa.ljl.weiwa.activity.MainActivity
import com.weiwa.ljl.weiwa.network.WeiboPojo
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.net.URL

/**
 * Created by hzfd on 2017/4/14.
 */

class CirclePortraitView : CircleImageView {
    private var mUser: WeiboPojo.User? = null
    private var instance: MainActivity? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {}

    fun addDownloadTask(downloadUrl: String) {
        async(CommonPool) {
            var myFileURL: URL? = null
            try {
                myFileURL = URL(downloadUrl)
                if (!downloadUrl.toLowerCase().endsWith(".gif")) {
                    myFileURL = URL(downloadUrl.replace("thumbnail", "bmiddle"))
                }
                //if image cached
                val cachedFile = BitmapDownloadHelper.getCachedImage(WeiwaApplication.getFileName(myFileURL), BitmapDownloadHelper.PORTRAIT)
                if (cachedFile != null) {
                    val bitmap = BitmapDownloadHelper.adjustBitmap(BitmapFactory.decodeFile(cachedFile.absolutePath))
                    instance!!.runOnUiThread {
                        setImageBitmap(bitmap)
                    }
                } else {
                    val bitmap = BitmapDownloadHelper.adjustBitmap(BitmapFactory.decodeFile(BitmapDownloadHelper.downloadFileFromURL(myFileURL, WeiwaApplication.getFileName(myFileURL), BitmapDownloadHelper.PORTRAIT).absolutePath))
                    instance!!.runOnUiThread {
                        setImageBitmap(bitmap)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
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
