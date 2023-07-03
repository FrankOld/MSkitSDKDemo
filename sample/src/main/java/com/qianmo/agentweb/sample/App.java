package com.qianmo.agentweb.sample;

import android.app.Application;

import com.qianmo.agentweb.sample.utils.ConfigUtils;
import com.qianmo.agentweb.sample.utils.SDKDemoEnvUtils;

/**
 * Created by wangxianghui on 2017/5/23.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ConfigUtils configUtils = ConfigUtils.getInstance(this);
        SDKDemoEnvUtils.setApplication(this);
    }

}
