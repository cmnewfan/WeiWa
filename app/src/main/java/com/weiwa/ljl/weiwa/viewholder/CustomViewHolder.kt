package com.weiwa.ljl.weiwa.viewholder

import android.app.Fragment
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.WeiwaApplication
import com.weiwa.ljl.weiwa.activity.MainActivity
import com.weiwa.ljl.weiwa.fragment.ImageFragment
import com.weiwa.ljl.weiwa.listener.onAdapterEvent
import com.weiwa.ljl.weiwa.network.WeiboPojo
import com.weiwa.ljl.weiwa.view.PortraitView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by hzfd on 2017/4/13.
 */

open class CustomViewHolder : RecyclerView.ViewHolder {
    var context: Fragment? = null
        private set
    var mParentView: View? = null
    internal var adapterEvent: onAdapterEvent
    internal var id: String? = null

    internal constructor(itemView: View, fragment: Fragment, event: onAdapterEvent) : super(itemView) {
        context = fragment
        adapterEvent = event
    }

    constructor(itemView: View, fragment: Fragment, event: onAdapterEvent, wid: String) : super(itemView) {
        context = fragment
        adapterEvent = event
        id = wid
    }

    fun getUriOfBitmap(tempBitmap: Bitmap): Uri? {
        val png_file = File(WeiwaApplication.CacheCategory.absolutePath + "/temp.png")
        if (png_file.exists()) {
            png_file.delete()
        }
        try {
            png_file.createNewFile()
            val fos = FileOutputStream(png_file)
            tempBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            return Uri.fromFile(png_file)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }

    fun getBitmapFromView(view: View): Bitmap {
        //Define a bitmap with the same size as the view
        val returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_4444)
        //Bind a canvas to it
        val canvas = Canvas(returnedBitmap)
        //Get the view's background
        val bgDrawable = view.getBackground()
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            //bgDrawable.draw(canvas);
            canvas.drawColor(Color.WHITE)
        } else
        //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE)
        // draw the view on the canvas
        view.draw(canvas)
        //return the bitmap
        return returnedBitmap
    }

    open fun setId(w_id: String) {
        id = w_id
    }

    open fun refreshImage(urls: Array<WeiboPojo.Pic_urls?>?, convert_uris: Array<Uri>?, line_1: GridLayout, line_2: GridLayout, line_3: GridLayout) {
        line_1.setVisibility(View.GONE)
        line_2.setVisibility(View.GONE)
        line_3.setVisibility(View.GONE)
        line_1.removeAllViews()
        line_2.removeAllViews()
        line_3.removeAllViews()
        if (urls != null && urls.isNotEmpty()) {
            var count = 0
            for (url in urls) {
                val imageView = PortraitView(context!!.getActivity(), url!!.thumbnail_pic!!, urls.size, 0)
                val index = count
                imageView.setOnClickListener {
                    val fragment = ImageFragment()
                    val bundle = Bundle()
                    bundle.putParcelableArray("Uris", convert_uris)
                    bundle.putInt("Index", index)
                    fragment.setArguments(bundle)
                    (context!!.getActivity() as MainActivity).setFragment(fragment)
                }
                imageView.setLayoutParams(ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                val view = View(context!!.activity)
                view.setLayoutParams(ViewGroup.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT))
                view.background = null
                if (count < 3) {
                    line_1.addView(imageView)
                    line_1.addView(view)
                    line_1.setVisibility(View.VISIBLE)
                } else if (count < 6) {
                    line_2.addView(imageView)
                    line_2.addView(view)
                    line_2.setVisibility(View.VISIBLE)
                } else {
                    line_3.addView(imageView)
                    line_3.addView(view)
                    line_3.setVisibility(View.VISIBLE)
                }
                count++
            }
        }
    }

    /**
     * @param type             comment or repost
     * *                         分为转发和评论两种,根据type选择event
     * *
     * @param repost_user_name only use for repost RetweetedWeibo, other set null
     * *                         只在转发RetweetedWeibo时有用
     * *
     * @param repost_content   only use for repost RetweetedWeibo, other set null
     * *                         只在转发RetweetedWeibo时有用
     * *
     * @param true_id          only use for comment RetweetedWeibo, other set null. Cause this comment needs id of commented weibo, not original weibo.
     * *                         need to refactor.
     * *                         只在评论RetweetedWeibo时有用，因为其余Weibo的id都是原Weibo的ID，但在评论RetweetedWeibo时,需要切换为RetweetWeibo的ID。
     * *                         结构混乱，后期重构。
     * *
     * @return created popup window
     */
    internal fun createPopupWindow(type: Int, repost_user_name: String?, repost_content: String?, true_id: String?): PopupWindow {
        val popupWindow = PopupWindow()
        val view = ((context!!.getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)) as LayoutInflater).inflate(R.layout.popup_input, null)
        val confirm = view.findViewById(R.id.confirm) as Button
        val cancel = view.findViewById(R.id.cancel) as Button
        val input = view.findViewById(R.id.input) as EditText
        input.setFocusable(true)
        input.setOnClickListener { v ->
            val manager = context!!.getActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.showSoftInput(v, InputMethodManager.SHOW_FORCED)
            manager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }
        if (repost_user_name != null) {
            input.setText("//@" + repost_user_name + ":" + repost_content)
        }
        if (type == PopupWindow_Comment) {
            input.setHint(R.string.comment_this)
        } else if (type == PopupWindow_Repost) {
            input.setHint(R.string.repost_this)
        }
        confirm.setOnClickListener {
            if (input.getText().length == 0) {
                if (type == PopupWindow_Comment) {
                    if (true_id != null) {
                        adapterEvent.onComment(context!!.getActivity().getResources().getString(R.string.comment_this), true_id)
                    } else {
                        adapterEvent.onComment(context!!.getActivity().getResources().getString(R.string.comment_this), id!!)
                    }
                } else if (type == PopupWindow_Repost) {
                    if (repost_user_name == null) {
                        adapterEvent.onRepost(context!!.getActivity().getResources().getString(R.string.repost_this), id!!)
                    } else {
                        adapterEvent.onRepost(context!!.getActivity().getResources().getString(R.string.repost_this), id!!)
                    }
                }
            } else if (input.getText().length < 140) {
                if (type == PopupWindow_Comment) {
                    if (true_id != null) {
                        adapterEvent.onComment(input.getText().toString(), true_id)
                    } else {
                        adapterEvent.onComment(input.getText().toString(), id!!)
                    }
                } else if (type == PopupWindow_Repost) {
                    if (repost_user_name == null) {
                        adapterEvent.onRepost(input.getText().toString(), id!!)
                    } else {
                        adapterEvent.onRepost(input.getText().toString(), id!!)
                    }
                }
            } else {
                Toast.makeText(context!!.getActivity(), "超出字数限制" + (input.getText().length - 140).toString() + "字", Toast.LENGTH_SHORT).show()
            }
            popupWindow.dismiss()
        }
        cancel.setOnClickListener {
            popupWindow.dismiss()
        }
        popupWindow.setContentView(view)
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.setFocusable(true)
        popupWindow.setBackgroundDrawable(context!!.getActivity().getResources().getDrawable(R.drawable.popupwindow_drawable))
        popupWindow.setTouchInterceptor { v, event ->
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss()
                }
                true
            }
            false
        }
        return popupWindow
    }

    internal companion object {
        val PopupWindow_Comment = 1
        val PopupWindow_Repost = 2
    }
}
