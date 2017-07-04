package com.weiwa.ljl.weiwa.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.weiwa.ljl.weiwa.R
import com.weiwa.ljl.weiwa.WeiwaApplication

class SplashActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("EXIT", false)) {
            this.finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        } else {
            setContentView(R.layout.activity_splash)
            if (!WeiwaApplication.CacheCategory.exists()) {
                WeiwaApplication.CacheCategory.mkdir()
            }
            if (!WeiwaApplication.CachePortrait.exists()) {
                WeiwaApplication.CachePortrait.mkdir()
            }
            val intent = Intent()
            intent.setClass(this, MainActivity::class.java)
            val thread = object : Thread() {
                override fun run() {
                    try {
                        sleep(1500)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }

                    startActivity(intent)
                }
            }
            thread.start()
        }
    }
}
