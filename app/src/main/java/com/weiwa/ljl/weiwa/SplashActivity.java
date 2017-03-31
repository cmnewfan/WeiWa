package com.weiwa.ljl.weiwa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            setContentView(R.layout.activity_splash);
            if (!WeiwaApplication.CacheCategory.exists()) {
                WeiwaApplication.CacheCategory.mkdir();
            }
            if (!WeiwaApplication.CachePortrait.exists()) {
                WeiwaApplication.CachePortrait.mkdir();
            }
            final Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        this.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            };
            thread.start();
        }
    }
}
