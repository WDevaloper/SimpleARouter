package com.wfy.simple.arouter;

import android.app.Application;

import com.wfy.simple.router.api.Router;

/**
 * @Describe:
 * @Author: wfy
 * @Version: Create time: (wfy) on 2019/6/25 0:59
 * company :
 */
public class RouterApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Router.init(this);
    }
}
