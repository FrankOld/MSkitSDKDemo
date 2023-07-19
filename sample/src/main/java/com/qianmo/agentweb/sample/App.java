package com.qianmo.agentweb.sample;

import android.app.Application;

import com.qianmo.agentweb.sample.utils.ConfigUtils;
import com.qianmo.agentwebX5.MSkitWebX5;

/**
 * Created by wangxianghui on 2017/5/23.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MSkitWebX5.init(this, ConfigUtils.getInstance(this).getSDKChannelKey(), MSkitWebX5.SdkAccessMode.DEFAULT, BuildConfig.DEBUG);
    }

}
