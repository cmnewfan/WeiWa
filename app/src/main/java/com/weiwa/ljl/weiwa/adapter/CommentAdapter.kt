package com.weiwa.ljl.weiwa.adapter

import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.network.WeiboCommentPojo
import com.weiwa.ljl.weiwa.viewholder.CommentViewHolder

/**
 * Created by hzfd on 2017/2/8.
 */
class CommentAdapter(private val commentPojo: WeiboCommentPojo, private val mContext: Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_weibo, parent, false)
        return CommentViewHolder(view, mContext)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (position >= 0) {
            val item = holder as CommentViewHolder
            item.refresh(commentPojo.comments!![position])
        }
    }

    override fun getItemCount(): Int {
        if (commentPojo.comments == null) {
            return -1
        } else {
            return commentPojo.comments!!.size
        }
    }
}
