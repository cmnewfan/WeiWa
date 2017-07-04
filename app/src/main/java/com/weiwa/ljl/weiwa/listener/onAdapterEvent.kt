package com.weiwa.ljl.weiwa.listener

/**
 * Created by hzfd on 2017/1/22.
 */
interface onAdapterEvent {
    fun onNeedInsert()
    fun onNeedComment(id: String)
    fun onComment(content: String, id: String)
    fun onRepost(content: String, id: String)
}
