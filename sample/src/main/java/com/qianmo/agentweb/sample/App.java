package com.qianmo.agentweb.sample;

import android.app.Application;

import com.qianmo.agentweb.MSkitWeb;
import com.qianmo.agentweb.sample.utils.ConfigUtils;

/**
 * Created by wangxianghui on 2017/5/23.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MSkitWeb.init(this, ConfigUtils.getInstance(this).getSDKChannelKey(), MSkitWeb.SdkAccessMode.OWN, BuildConfig.DEBUG);
    }

}
