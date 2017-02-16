package com.weiwa.ljl.weiwa;

import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

/**
 * Created by hzfd on 2017/2/13.
 */
public interface OnUserUpdatedListener {
    void onUpdate(WeiboPojo pojo, int updateType);
}
