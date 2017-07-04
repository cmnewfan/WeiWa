package com.weiwa.ljl.weiwa.network

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.sina.weibo.sdk.auth.Oauth2AccessToken
import com.sina.weibo.sdk.auth.WeiboAuthListener
import com.sina.weibo.sdk.exception.WeiboException
import com.weiwa.ljl.weiwa.activity.MainActivity

/**
 * Created by hzfd on 2017/1/16.
 */
class WeiWaAuthListener(private val mContext: Activity) : WeiboAuthListener {
    internal var mAccessToken: Oauth2AccessToken? = null
    override fun onComplete(bundle: Bundle) {
        mAccessToken = Oauth2AccessToken.parseAccessToken(bundle) // 从 Bundle 中解析 Token
        if (mAccessToken!!.isSessionValid) {
            MainActivity.token = mAccessToken!!.token
            val sharedPreferences = mContext.getSharedPreferences("weibo_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("Token", mAccessToken!!.token)
            editor.putString("Uid", mAccessToken!!.uid)
            editor.commit()
        } else {
            // 当您注册的应用程序签名不正确时，就会收到错误Code，请确保签名正确
            Toast.makeText(mContext, bundle.getString("code", ""), Toast.LENGTH_SHORT)
        }
    }

    override fun onWeiboException(e: WeiboException) {
        Log.e("Error", e.message)
    }

    override fun onCancel() {

    }
}
