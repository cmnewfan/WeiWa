package com.weiwa.ljl.weiwa.viewholder

import android.app.Fragment
import android.content.Intent
import android.graphics.Paint
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.TextView
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.listener.onAdapterEvent
import com.weiwa.ljl.weiwa.network.WeiboPojo
import com.weiwa.ljl.weiwa.view.CirclePortraitView

/**
 * Created by hzfd on 2017/4/13.
 */

class SingleWB_ViewHolder : CustomViewHolder {
    internal val user_name: TextView by lazy { itemView.findViewById(R.id.single_user) as TextView }
    internal val user_portrait: CirclePortraitView by lazy { itemView.findViewById(R.id.single_user_portrait) as CirclePortraitView }
    internal val text: TextView by lazy { itemView.findViewById(R.id.weibo_text) as TextView }
    internal val date: TextView by lazy { itemView.findViewById(R.id.weibo_date) as TextView }
    internal val repost: TextView by lazy { itemView.findViewById(R.id.weibo_repost_count) as TextView }
    internal val comment: TextView by lazy { itemView.findViewById(R.id.weibo_comment_count) as TextView }
    internal val line_1: GridLayout by lazy { itemView.findViewById(R.id.weibo_image_line_1) as GridLayout }
    internal val line_2: GridLayout by lazy { itemView.findViewById(R.id.weibo_image_line_2) as GridLayout }
    internal val line_3: GridLayout by lazy { itemView.findViewById(R.id.weibo_image_line_3) as GridLayout }
    internal var user: WeiboPojo.User? = null
    internal val option: ImageButton by lazy { itemView.findViewById(R.id.option) as ImageButton }

    constructor(itemView: View, context: Fragment, event: onAdapterEvent) : super(itemView, context, event) {
        mParentView = itemView
        initView()
    }

    constructor(itemView: View, context: Fragment, event: onAdapterEvent, wid: String) : super(itemView, context, event, wid) {
        mParentView = itemView
        initView()
    }

    private fun initView() {
        option.setOnClickListener {
            val popupMenu = PopupMenu(context!!.activity, option)
            popupMenu.inflate(R.menu.menu_weibo)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.comment_this -> createPopupWindow(CustomViewHolder.Companion.PopupWindow_Comment, null, null, null).showAtLocation(user_name, Gravity.CENTER, 0, 0)
                    R.id.repost_this -> createPopupWindow(CustomViewHolder.Companion.PopupWindow_Repost, null, null, null).showAtLocation(user_name, Gravity.CENTER, 0, 0)
                    R.id.share_this -> {
                        val targetUri = getUriOfBitmap(getBitmapFromView(mParentView!!))
                        val sendIntent = Intent(Intent.ACTION_SEND)
                        //set intent type
                        sendIntent.type = "image/*"
                        sendIntent.putExtra(Intent.EXTRA_STREAM, targetUri)
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "分享")
                        sendIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context!!.startActivity(Intent.createChooser(sendIntent, "share"))
                    }
                }
                true
            }
            popupMenu.show()
        }
        repost.setOnClickListener { setRepostUnderline() }
        comment.setOnClickListener { setCommentUnderline() }
    }

    fun refresh(statuses: WeiboPojo.Statuses) {
        user_name.text = statuses.user!!.name
        user_portrait.addDownloadTask(statuses.user!!.profile_image_url!!, context!!.activity, statuses.user!!)
        text.text = statuses.text
        date.text = statuses.created_at
        repost.text = statuses.reposts_count
        comment.text = statuses.comments_count
        refreshImage(statuses.pic_urls, statuses.convertToUris(), line_1, line_2, line_3)
    }

    fun setCommentUnderline() {
        comment.paintFlags = comment.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        repost.paintFlags = repost.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    fun setRepostUnderline() {
        repost.paintFlags = repost.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        comment.paintFlags = comment.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    fun setUser(wUser: WeiboPojo.User) {
        //user_portrait.setData(context!!.activity as MainActivity, wUser)
    }
}
