package com.weiwa.ljl.weiwa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.sina.weibo.sdk.api.MusicObject;
import com.weiwa.ljl.weiwa.WeiboModel.WeiboPojo;

/**
 * Created by hzfd on 2017/2/13.
 */
public class PortraitView extends ImageView {
    private WeiboPojo.User mUser;
    private MainActivity instance;

    public PortraitView(Context context, final WeiboPojo.User user) {
        super(context);
        instance = (MainActivity) context;
        mUser = user;
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setData(MainActivity context, WeiboPojo.User user){
        instance = context;
        mUser = user;
        /*setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UserViewFragment userViewFragment = new UserViewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", mUser.getScreen_name());
                bundle.putString("decription",mUser.getDescription());
                bundle.putString("location",mUser.getLocation());
                bundle.putString("gender",mUser.getGender());
                bundle.putString("follow_me",mUser.getFollow_me());
                bundle.putString("uid",mUser.getId());
                userViewFragment.setArguments(bundle);
                instance.setFragment(userViewFragment);
            }
        });*/
    }
}
