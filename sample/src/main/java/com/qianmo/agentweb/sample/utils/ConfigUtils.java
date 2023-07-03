package com.qianmo.agentweb.sample.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by york on 2019-06-26.
 */
public class ConfigUtils {

    public static final String[] ENVIR_TITLES = new String[]{"开发环境", "现网环境"};

    private static final String[] LOGIN_URL_ARRY = new String[]{
            "https://test.api.mansenwenhua77.com/",
            "https://api.pro.mansenwenhua77.com/"
    };

    private static final String[] SDK_URL_ARRY = new String[]{
            "https://test.h5.mansenwenhua77.com/",
            "https://h5.api.mansenwenhua77.com/"
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

    public void setEnvir(int envir) {
        mPreferences.edit().putInt(ENVIR_KEY, envir).commit();
    }

    public int getEnvir() {
        return mPreferences.getInt(ENVIR_KEY, 0);
    }

    public String getLoginUrl() {
        int envirCode = mPreferences.getInt(ENVIR_KEY, 0);
        return LOGIN_URL_ARRY[envirCode] + "auth/oauth/token";
    }

    public String getSDKUrl() {
        int envirCode = mPreferences.getInt(ENVIR_KEY, 0);
        return SDK_URL_ARRY[envirCode];
    }


}