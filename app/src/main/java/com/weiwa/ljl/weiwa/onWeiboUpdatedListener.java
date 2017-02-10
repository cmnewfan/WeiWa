package com.weiwa.ljl.weiwa;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboCommentPojo;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

/**
 * Created by hzfd on 2017/1/17.
 */
public interface onWeiboUpdatedListener {
    void onUpdate(WeiboPojo pojo, int updateType);
    void onComment(WeiboCommentPojo comment);
}
