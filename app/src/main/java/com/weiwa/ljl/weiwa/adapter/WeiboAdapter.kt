package com.weiwa.ljl.weiwa.adapter

import android.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.fragment.MainFragment
import com.weiwa.ljl.weiwa.listener.onAdapterEvent
import com.weiwa.ljl.weiwa.network.WeiboPojo
import com.weiwa.ljl.weiwa.viewholder.Retweeted_ViewHolder
import com.weiwa.ljl.weiwa.viewholder.SingleWB_ViewHolder

/**
 * Created by hzfd on 2017/1/17.
 */
class WeiboAdapter(private var weibo_data: WeiboPojo?, private val mContext: Fragment, private val onNeedInsert: onAdapterEvent) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val SINGLE_WB = 0
    private val RETWEETED_WB = 1
    private val animation: Animation
    private var enableGetNewWeibo: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SINGLE_WB) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.single_weibo, parent, false)
            return SingleWB_ViewHolder(view, mContext, onNeedInsert)
        } else if (viewType == RETWEETED_WB) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.retweeted_weibo, parent, false)
            return Retweeted_ViewHolder(view, mContext, onNeedInsert)
        } else {
            throw NullPointerException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (weibo_data!!.statuses!![position].retweeted_status != null && weibo_data!!.statuses!![position].retweeted_status!!.id != null) {
            return RETWEETED_WB
        } else {
            return SINGLE_WB
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {

                }

                override fun onAnimationEnd(animation: Animation) {

                }

                override fun onAnimationRepeat(animation: Animation) {

                }
            })
            (mContext as? MainFragment)?.fadeRecyclerView(position)
            onNeedInsert.onNeedComment(weibo_data!!.statuses!![position].id!!)
        }
        when (holder.itemViewType) {
            SINGLE_WB -> {
                val single_item = holder as SingleWB_ViewHolder
                single_item.refresh(weibo_data!!.statuses!![position])
                single_item.setId(weibo_data!!.statuses!![position].id!!)
                single_item.setUser(weibo_data!!.statuses!![position].getUser())
            }
            RETWEETED_WB -> {
                val retweet_item = holder as Retweeted_ViewHolder
                retweet_item.refresh(weibo_data!!.statuses!![position])
                retweet_item.setRetweetId(weibo_data!!.statuses!![position].id!!)
                retweet_item.setId(weibo_data!!.statuses!![position].retweeted_status!!.id!!)
                retweet_item.setUser(weibo_data!!.statuses!![position].getUser(), weibo_data!!.statuses!![position].retweeted_status!!.user!!)
            }
            else -> {
            }
        }
        if (position == weibo_data!!.statuses!!.size - 1 && enableGetNewWeibo) {
            onNeedInsert.onNeedInsert()
        }
    }

    override fun getItemCount(): Int {
        return weibo_data!!.statuses!!.size
    }

    init {
        this.animation = AnimationUtils.loadAnimation(mContext.activity, R.anim.fade_in)
    }

    fun insertNewData(newData: WeiboPojo) {
        val length = weibo_data!!.statuses!!.size
        val updatedData = arrayOfNulls<WeiboPojo.Statuses>(weibo_data!!.statuses!!.size + newData.statuses!!.size)
        for (i in updatedData.indices) {
            if (i < weibo_data!!.statuses!!.size) {
                updatedData[i] = weibo_data!!.statuses!![i]
            } else {
                updatedData[i] = newData.statuses!![i - weibo_data!!.statuses!!.size]
            }
        }
        newData.statuses = updatedData.map { it!! }.toTypedArray()
        this.weibo_data = newData
        notifyItemRangeInserted(length, newData.statuses!!.size)
    }

    fun setData(data: WeiboPojo, enable: Boolean) {
        weibo_data = data
        enableGetNewWeibo = enable
        notifyDataSetChanged()
    }
}
