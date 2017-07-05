package com.weiwa.ljl.weiwa.viewholder

import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.network.WeiboCommentPojo
import com.weiwa.ljl.weiwa.view.PortraitView

/**
 * Created by hzfd on 2017/4/13.
 */

class CommentViewHolder(itemView: View, internal var mContext: Fragment) : RecyclerView.ViewHolder(itemView) {
    private var comment_portrait: PortraitView = itemView.findViewById(R.id.single_user_portrait) as PortraitView
    private var comment_name: TextView = itemView.findViewById(R.id.single_user) as TextView
    private var comment_date: TextView = itemView.findViewById(R.id.weibo_date) as TextView
    private var comment_text: TextView = itemView.findViewById(R.id.comment_text) as TextView

    fun refresh(comment: WeiboCommentPojo.Comments) {
        comment_name.text = comment.user!!.name
        comment_date.text = comment.created_at
        comment_text.text = comment.text
        comment_portrait.addDownloadTask(comment.user!!.profile_image_url!!, 1, 1, mContext.activity, comment.user!!)
    }
}
