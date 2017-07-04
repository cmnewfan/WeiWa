package com.weiwa.ljl.weiwa.view

import android.content.Context
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import java.util.*

/**
 * Created by hzfd on 2017/2/15.
 */
class ImageViewPager : ViewPager {

    private var mUris: Array<Uri>? = null
    private var mContext: Context? = null
    private var mListener: onImageDownladingListener? = null
    private var mIndex: Int = 0

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    fun init(context: Context, uris: Array<Uri>, listener: onImageDownladingListener, index: Int) {
        mContext = context
        mUris = uris
        mListener = listener
        InitViewAdapter()
        mIndex = index
        currentItem = mIndex
    }

    override fun onTouchEvent(me: MotionEvent): Boolean {
        try {
            super.onTouchEvent(me)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onInterceptTouchEvent(me: MotionEvent): Boolean {
        try {
            super.onTouchEvent(me)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        var height = 0
        for (i in 0..childCount - 1) {
            val child = getChildAt(i)
            //child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            val h = child.measuredHeight
            if (h > height) {
                height = h
            }
        }
        if (height == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun InitViewAdapter() {
        val titleList = ArrayList<String>()
        titleList.add("")
        titleList.add("")
        val guide_pager_adapter = object : PagerAdapter() {
            override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
                return arg0 === arg1 as View
            }

            override fun getCount(): Int {
                return mUris!!.size
            }

            override fun destroyItem(container: ViewGroup, position: Int,
                                     `object`: Any) {
                container.removeView(`object` as View)
            }

            override fun getItemPosition(`object`: Any?): Int {
                return super.getItemPosition(`object`)
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val imageView = com.weiwa.ljl.weiwa.view.TouchImageView(mContext!!)
                imageView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                container.addView(imageView)
                mListener!!.onDownload(mUris!![position], imageView)
                return imageView
            }
        }
        adapter = guide_pager_adapter
    }

    interface onImageDownladingListener {
        fun onDownload(uri: Uri, imageView: ImageView)
    }
}
