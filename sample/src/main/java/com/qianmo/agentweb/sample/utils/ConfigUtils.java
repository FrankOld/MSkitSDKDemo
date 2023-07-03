package com.qianmo.agentweb.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigUtils {

    private static final String[] LOGIN_URL_ARRY = new String[]{
            "https://api.pro.mansenwenhua77.com/"
    };

    private static final String[] SDK_URL_ARRY = new String[]{
            "https://h5.api.mansenwenhua77.com/"
    };

    private static final String[] SDK_CHANNEL_KEY = new String[]{
            "SDKTest"
    };

    private static final String ENVIR_KEY = "ENVIR_KEY";

    private static SharedPreferences mPreferences;
    private static ConfigUtils mConfigUtils = null;

    private ConfigUtils(Context context) {
        mPreferences = context.getSharedPreferences("envir_switch", Context.MODE_PRIVATE);
    }

    public static ConfigUtils getInstance(Context context) {
        if (mConfigUtils == null) {
            mConfigUtils = new ConfigUtils(context);
        }
        return mConfigUtils;
    }

    public String getLoginUrl() {
        int envirCode = mPreferences.getInt(ENVIR_KEY, 0);
        return LOGIN_URL_ARRY[envirCode] + "auth/oauth/token";
    }

    public String getSDKUrl() {
        int envirCode = mPreferences.getInt(ENVIR_KEY, 0);
        return SDK_URL_ARRY[envirCode];
    }

    public String getSDKChannelKey() {
        int envirCode = mPreferences.getInt(ENVIR_KEY, 0);
        return SDK_CHANNEL_KEY[envirCode];
    }

}