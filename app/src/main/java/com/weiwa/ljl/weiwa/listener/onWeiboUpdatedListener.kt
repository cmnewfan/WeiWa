package com.weiwa.ljl.weiwa.listener

import com.weiwa.ljl.weiwa.network.WeiboCommentPojo
import com.weiwa.ljl.weiwa.network.WeiboPojo

/**
 * Created by hzfd on 2017/1/17.
 */
interface onWeiboUpdatedListener {
    fun onUpdate(pojo: WeiboPojo, updateType: Int)
    fun onComment(comment: WeiboCommentPojo)
}
