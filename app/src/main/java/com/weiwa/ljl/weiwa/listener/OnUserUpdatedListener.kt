package com.weiwa.ljl.weiwa.listener

import com.weiwa.ljl.weiwa.network.WeiboPojo

/**
 * Created by hzfd on 2017/2/13.
 */
interface OnUserUpdatedListener {
    fun onUpdate(pojo: WeiboPojo, updateType: Int)
}
