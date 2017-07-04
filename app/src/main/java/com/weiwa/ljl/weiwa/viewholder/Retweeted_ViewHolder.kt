package com.weiwa.ljl.weiwa.viewholder

import android.app.Fragment
import android.content.Intent
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

class Retweeted_ViewHolder : CustomViewHolder {
    internal val retweet_user: TextView by lazy { itemView.findViewById(R.id.retweet_user) as TextView }
    internal val retweeted_user: TextView by lazy { itemView.findViewById(R.id.retweeted_user) as TextView }
    internal val retweet_user_portrait: CirclePortraitView by lazy { itemView.findViewById(R.id.retweet_user_portrait) as CirclePortraitView }
    internal val retweeted_user_portrait: CirclePortraitView by lazy { itemView.findViewById(R.id.retweeted_user_portrait) as CirclePortraitView }
    internal val retweet_text: TextView by lazy { itemView.findViewById(R.id.retweet_weibo_text) as TextView }
    internal val retweet_date: TextView by lazy { itemView.findViewById(R.id.retweet_weibo_date) as TextView }
    internal val retweeted_text: TextView by lazy { itemView.findViewById(R.id.retweeted_weibo_text) as TextView }
    internal val retweeted_date: TextView by lazy { itemView.findViewById(R.id.retweeted_weibo_date) as TextView }
    internal val retweeted_repost: TextView by lazy { itemView.findViewById(R.id.retweeted_repost_text) as TextView }
    internal val retweeted_comment: TextView by lazy { itemView.findViewById(R.id.retweeted_comment_text) as TextView }
    internal val retweeted_line_1: GridLayout by lazy { itemView.findViewById(R.id.retweeted_weibo_image_line_1) as GridLayout }
    internal val retweeted_line_2: GridLayout by lazy { itemView.findViewById(R.id.retweeted_weibo_image_line_2) as GridLayout }
    internal val retweeted_line_3: GridLayout by lazy { itemView.findViewById(R.id.retweeted_weibo_image_line_3) as GridLayout }
    internal var retweet_id: String? = null
    internal val option: ImageButton by lazy { itemView.findViewById(R.id.option) as ImageButton }

    constructor(itemView: View, context: Fragment, event: onAdapterEvent) : super(itemView, context, event) {
        mParentView = itemView
    }

    constructor(itemView: View, context: Fragment, event: onAdapterEvent, wid: String) : super(itemView, context, event, wid) {
        mParentView = itemView
    }

    fun setRetweetId(id: String) {
        retweet_id = id
    }

    fun refresh(statuses: WeiboPojo.Statuses) {
        retweet_date.text = statuses.created_at
        retweet_text.text = statuses.text
        retweeted_date.text = statuses.retweeted_status!!.created_at
        retweeted_text.text = statuses.retweeted_status!!.text
        retweet_user.text = statuses.getUser().name
        option.setOnClickListener {
            val popupMenu = PopupMenu(context!!.activity, option)
            popupMenu.inflate(R.menu.menu_weibo)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.comment_this -> createPopupWindow(CustomViewHolder.Companion.PopupWindow_Comment, null, null, retweet_id).showAtLocation(retweet_user, Gravity.CENTER, 0, 0)
                    R.id.repost_this -> createPopupWindow(CustomViewHolder.Companion.PopupWindow_Repost, retweet_user.text.toString(), retweet_text.text.toString(), null).showAtLocation(retweet_user, Gravity.CENTER, 0, 0)
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
        this.retweeted_user.text = statuses.retweeted_status!!.user!!.name
        this.retweet_user_portrait.addDownloadTask(statuses.getUser().profile_image_url!!, context!!.activity, statuses.getUser())
        this.retweeted_user_portrait.addDownloadTask(statuses.retweeted_status!!.user!!.profile_image_url!!, context!!.activity, statuses.retweeted_status!!.user!!)
        refreshImage(statuses.retweeted_status!!.pic_urls!!, statuses.retweeted_status!!.convertToUris(), this.retweeted_line_1, this.retweeted_line_2, this.retweeted_line_3)
    }

    fun setUser(repostUser: WeiboPojo.User, repostedUser: WeiboPojo.User) {
        //retweeted_user_portrait.setData(context!!.activity as MainActivity, repostedUser)
        //retweet_user_portrait.setData(context!!.activity as MainActivity, repostUser)
    }
}
