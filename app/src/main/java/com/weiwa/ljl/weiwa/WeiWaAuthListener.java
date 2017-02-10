package com.weiwa.ljl.weiwa;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * Created by hzfd on 2017/1/16.
 */
public class WeiWaAuthListener implements WeiboAuthListener {
    Oauth2AccessToken mAccessToken;
    private Activity mContext;
    @Override
    public void onComplete(Bundle bundle) {
        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle); // 从 Bundle 中解析 Token
        if (mAccessToken.isSessionValid()) {
            MainActivity.token = mAccessToken.getToken();
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("weibo_preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Token",mAccessToken.getToken());
            editor.commit();
        } else {
            // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
            Toast.makeText(mContext,bundle.getString("code", ""),Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onWeiboException(WeiboException e) {
        Log.e("Error",e.getMessage());
    }

    @Override
    public void onCancel() {

    }

    public WeiWaAuthListener(Activity context){
        mContext = context;
    }
}
