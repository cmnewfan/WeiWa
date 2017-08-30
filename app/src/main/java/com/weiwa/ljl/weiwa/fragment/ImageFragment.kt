package com.weiwa.ljl.weiwa.fragment


import android.app.Fragment
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.diegocarloslima.byakugallery.lib.TileBitmapDrawable
import com.wang.avi.AVLoadingIndicatorView
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.WeiwaApplication
import com.weiwa.ljl.weiwa.network.NetworkBroadcastReceiver
import com.weiwa.ljl.weiwa.view.ImageViewPager
import com.weiwa.ljl.weiwa.view.TouchImageView
import uk.co.senab.photoview.PhotoViewAttacher
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.ExecutionException


/**
 * A simple [Fragment] subclass.
 */
class ImageFragment : Fragment() {

    private var mView: View? = null
    private var downloadFile: File? = null
    private val downloadCategory = File(Environment.getExternalStorageDirectory().absolutePath + "/Weiwa")
    private var mProgressText: TextView? = null
    private var mDownloadText: TextView? = null
    private var mShareButton: ImageButton? = null
    private var mViewPager: ImageViewPager? = null
    private var mAvi: AVLoadingIndicatorView? = null
    private var uris: Array<Uri>? = null
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!downloadCategory.exists()) {
            downloadCategory.mkdir()
        }
        if (arguments != null) {
            //init Uris
            uris = arguments.getParcelableArray("Uris") as Array<Uri>
            currentIndex = arguments.getInt("Index")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_image, container, false)
        mAvi = mView!!.findViewById(R.id.avi) as AVLoadingIndicatorView
        mShareButton = mView!!.findViewById(R.id.button_share) as ImageButton
        mShareButton!!.setOnClickListener {
            try {
                share()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        mProgressText = mView!!.findViewById(R.id.progress_text) as TextView
        mProgressText!!.text = (currentIndex + 1).toString() + "/" + uris!!.size
        mDownloadText = mView!!.findViewById(R.id.download_text) as TextView
        mViewPager = mView!!.findViewById(R.id.guide_viewpager) as ImageViewPager
        mViewPager!!.minimumWidth = RecyclerView.LayoutParams.MATCH_PARENT
        mViewPager!!.init(activity, uris!!, object : ImageViewPager.onImageDownladingListener {
            override fun onDownload(uri: Uri, imageView: ImageView) {
                try {
                    LoadGlideImage(uri, imageView)
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }
            }
        }, currentIndex)
        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                setProgressText((position + 1).toString() + "/" + uris!!.size)
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
        return mView
    }

    fun setProgressText(content: String) {
        mProgressText!!.text = content
    }

    @Throws(MalformedURLException::class)
    fun LoadGlideImage(targetUri: Uri, imageView: ImageView) {
        mAvi!!.show()
        if (isGif(targetUri)) {
            val imageViewTarget = GlideDrawableImageViewTarget(imageView)
            Glide.with(this).load(targetUri).downloadOnly<SimpleTarget<File>>(object : SimpleTarget<File>() {
                override fun onResourceReady(resource: File, glideAnimation: GlideAnimation<in File>) {
                    try {
                        val inputStream = FileInputStream(resource)
                        downloadFile = File(downloadCategory, WeiwaApplication.getFileName(targetUri) + ".gif")
                        val outputStream = FileOutputStream(downloadFile!!)
                        var bytesRead = 0
                        val buffer = ByteArray(8192)
                        while ({ bytesRead = inputStream.read(buffer, 0, 8192); bytesRead }() != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        inputStream.close()
                        outputStream.close()
                        mAvi!!.hide()
                    } catch (e: FileNotFoundException) {
                        mAvi!!.hide()
                        e.printStackTrace()
                    } catch (e: IOException) {
                        mAvi!!.hide()
                        e.printStackTrace()
                    }

                }
            })
            Glide.with(this).load(targetUri).diskCacheStrategy(DiskCacheStrategy.SOURCE).fitCenter().into(imageViewTarget)
        } else {
            if (!NetworkBroadcastReceiver.getWifiState()) {
                imageView.setImageDrawable(this.activity.getDrawable(R.drawable.ic_wait_for_download))
                imageView.setOnClickListener {
                    val bitmapDownloader = BitmapDownloader()
                    //current item position is used to display progress of item downloading
                    bitmapDownloader.execute(*arrayOf(URL(targetUri.toString()), imageView, mViewPager!!.currentItem))
                }
                return
            }
            val bitmapDownloader = BitmapDownloader()
            //current item position is used to display progress of item downloading
            bitmapDownloader.execute(*arrayOf(URL(targetUri.toString()), imageView, mViewPager!!.currentItem))
        }
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    private fun share() {
        val shareFile: File
        val sendIntent = Intent(Intent.ACTION_SEND)
        //set intent type
        sendIntent.type = "image/*"
        if (isGif(uris!![mViewPager!!.currentItem])) {
            shareFile = File(WeiwaApplication.CacheCategory.toString() + "/" + WeiwaApplication.getFileName(uris!![mViewPager!!.currentItem]) + ".gif")
        } else {
            shareFile = File(WeiwaApplication.CacheCategory.toString() + "/" + WeiwaApplication.getFileName(uris!![mViewPager!!.currentItem]))
        }
        val targetUri = Uri.fromFile(shareFile)
        if (targetUri != null) {
            sendIntent.putExtra(Intent.EXTRA_STREAM, targetUri)
        }
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享")
        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(Intent.createChooser(sendIntent, "share"))
    }

    private fun isGif(uri: Uri): Boolean {
        return uri.toString().toLowerCase().endsWith("gif")
    }

    internal inner class BitmapDownloader : AsyncTask<Any, Any, File>() {
        private var imageView: TouchImageView? = null
        private var url: URL? = null
        private var position: Int = 0
        override fun doInBackground(vararg params: Any): File? {
            val bitmap: Bitmap
            try {
                //获得连接
                url = params[0] as URL
                imageView = params[1] as TouchImageView
                position = params[2] as Int
                val conn = url!!.openConnection() as HttpURLConnection
                //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                conn.connectTimeout = 6000
                //连接设置获得数据流
                conn.doInput = true
                //不使用缓存
                conn.useCaches = false
                //得到数据流
                val `is` = conn.inputStream
                downloadFile = File(downloadCategory, WeiwaApplication.getFileName(url!!))
                val os = FileOutputStream(downloadFile!!)
                var bytesRead = 0
                val count = conn.contentLength
                val buffer = ByteArray(count)
                var progress = 0.0f
                var offset = 0
                var read = 8192
                if (read > count) {
                    read = count
                }
                while ({ bytesRead = `is`.read(buffer, offset, read); bytesRead }() != -1) {
                    os.write(buffer, offset, bytesRead)
                    offset += bytesRead
                    if (`is`.available() != 0) {
                        progress = offset.toFloat() / count
                    } else {
                        progress = offset.toFloat() / count
                    }
                    if (count - offset <= read) {
                        read = count - offset
                    }
                    if (WeiwaApplication.getFileName(url!!) == WeiwaApplication.getFileName(uris!![position])) {
                        publishProgress(progress)
                    }
                }
                os.close()
                `is`.close()
                return downloadFile
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
        }

        override fun onProgressUpdate(vararg values: Any) {
            val display_progress = (values[0] as Float * 100).toInt()
            mDownloadText!!.visibility = View.VISIBLE
            mDownloadText!!.text = display_progress.toString() + "%"
            if (display_progress == 1) {
                mDownloadText!!.text = "0"
                mDownloadText!!.visibility = View.INVISIBLE
            }
        }

        override fun onPostExecute(result: File?) {
            //GL11.GL_MAX_TEXTURE_SIZE
            if (result == null) {
                Toast.makeText(this@ImageFragment.activity, "文件下载失败", Toast.LENGTH_SHORT).show()
                mAvi!!.hide()
                return
            }
            if (imageView == null) {
                mAvi!!.hide()
                return
            }
            imageView!!.setImageURI(Uri.fromFile(result))
            if ((imageView!!.drawable.intrinsicHeight / imageView!!.drawable.intrinsicWidth).toFloat() > 2.0f) {
                val newBitmap = BitmapFactory.decodeFile(result.absolutePath)
                val newWidth = imageView!!.width
                val ratio = newWidth.toFloat() / newBitmap.width.toFloat()
                imageView!!.setMaxScale(ratio)
                TileBitmapDrawable.attachTileBitmapDrawable(imageView, result.absolutePath, null, null)
                if (WeiwaApplication.getFileName(url!!) == WeiwaApplication.getFileName(uris!![position])) {
                    imageView!!.performDoubleTap()
                }
            } else {
                val attacher = PhotoViewAttacher(imageView!!)
            }
            mAvi!!.hide()
        }
    }
}// Required empty public constructor
