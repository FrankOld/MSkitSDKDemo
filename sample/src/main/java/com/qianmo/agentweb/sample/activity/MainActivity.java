package com.qianmo.agentweb.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebStorage;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qianmo.agentweb.core.AgentWebConfig;
import com.qianmo.agentweb.sample.BuildConfig;
import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.sample.utils.ConfigUtils;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        findViewById(R.id.user_logout_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(true);
            }
        });

        findViewById(R.id.web_new_sdk_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebSDK();
            }
        });

        ((TextView) findViewById(R.id.tv_current_sdk_version)).setText(
                "当前SDK版本：" + AgentWebConfig.AGENTWEB_VERSION + "\n当前APP版本：" + BuildConfig.VERSION_NAME + "，build：" + BuildConfig.VERSION_CODE
        );

        AgentWebConfig.debug();
        if (AgentWebConfig.DEBUG) {
            Log.i(TAG, "Debug 模式");
        } else {
            Log.i(TAG, "release 模式");
        }
    }

    private void logout(boolean isShowToast) {
        // 测试Cookies
        try {
            String targetUrl = "";
            Log.i(TAG, "cookies:" + AgentWebConfig.getCookiesByUrl(targetUrl = getUrl()));
            AgentWebConfig.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                    Log.i(TAG, "onResume():" + value);
                }
            });

            String tagInfo = AgentWebConfig.getCookiesByUrl(targetUrl);
            Log.i(TAG, "tag:" + tagInfo);

            if (isShowToast) {
                Toast.makeText(MainActivity.this, "已登出，cookies: " + tagInfo, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "登出失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        //清空所有 AgentWeb 硬盘缓存，包括 WebView 的缓存 , AgentWeb 下载的图片 ，视频 ，apk 等文件。
        AgentWebConfig.clearDiskCache(this);
        WebStorage.getInstance().deleteAllData();
    }

    private String getUrl() {
        return ConfigUtils.getInstance(this).getSDKUrl();
    }

    private void gotoWebSDK() {
        startActivity(new Intent(MainActivity.this, CommonActivity.class));
    }

}