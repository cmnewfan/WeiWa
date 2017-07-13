package com.weiwa.ljl.weiwa.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager


/**
 * Created by hzfd on 2017/7/12.
 */

class NetworkBroadcastReceiver : BroadcastReceiver() {
    companion object {
        private var isWifi = false
        fun getWifiState(): Boolean {
            return isWifi
        }
    }

    fun init(context: Context) {
        var connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var info = connectivityManager.activeNetworkInfo
        isWifi = info.type == ConnectivityManager.TYPE_WIFI
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == ConnectivityManager.CONNECTIVITY_ACTION) {
            var connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var info = connectivityManager.activeNetworkInfo
            isWifi = if (info == null) false else info.type == ConnectivityManager.TYPE_WIFI
        }
    }
}
