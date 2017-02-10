package com.weiwa.ljl.weiwa;

/**
 * Created by hzfd on 2017/1/22.
 */
public interface onAdapterEvent {
    void onNeedInsert();
    void onNeedComment(String id);
    void onComment(String content,String id);
    void onRepost(String content,String id);
}
