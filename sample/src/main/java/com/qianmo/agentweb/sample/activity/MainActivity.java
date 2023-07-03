package com.qianmo.agentweb.sample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.qianmo.agentweb.core.AgentWebConfig;
import com.qianmo.agentweb.sample.BuildConfig;
import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.sample.net.LoginEntity;
import com.qianmo.agentweb.sample.net.OkHttpHelper;
import com.qianmo.agentweb.sample.net.SimpleCallBack;
import com.qianmo.agentweb.sample.utils.ConfigUtils;
import com.qianmo.agentweb.utils.SPUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private ConfigUtils mConfigUtils;

    private OkHttpHelper mOkHttpHelper;

    private LoadingPopupView loadingPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mConfigUtils = ConfigUtils.getInstance(this.getApplicationContext());
        mOkHttpHelper = OkHttpHelper.getInstance();

        findViewById(R.id.user_logout_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(true);
            }
        });

        findViewById(R.id.web_new_sdk_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(false);
                gotoWebSDK();
            }
        });
        findViewById(R.id.web_new_sdk_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoInputDialog();
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
    }

    private String getUrl() {
        return mConfigUtils.getSDKUrl();
    }

    private void showInfoInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_info, null);
        final EditText etSdkKey = view.findViewById(R.id.et_shop_url);
        final EditText etUserId = view.findViewById(R.id.et_user_id);

        etSdkKey.setText("SDKTest");
        etUserId.setText("UserIdTest");

        builder.setTitle("请输入以下信息");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initSDK(etSdkKey.getText().toString().trim());

                doLogin(etSdkKey.getText().toString().trim(), etUserId.getText().toString().trim());

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        builder.setView(view);

        AlertDialog dialog;
        dialog = builder.create();
        dialog.show();
    }

    private void initSDK(String channelKey) {
        SPUtils.getInstance(this, SPUtils.FILE_NAME);
        SPUtils.putData(SPUtils.KEY_SDK_KEY, channelKey);
    }

    // -------------------- 登录相关 start --------------------
    private void doLogin(String channelKey, String userId) {
        if (loadingPopup == null) {
            loadingPopup = new XPopup.Builder(this)
                    .dismissOnBackPressed(false)
                    .isLightNavigationBar(true)
                    .asLoading("登录中...", LoadingPopupView.Style.ProgressBar);
        }
        loadingPopup.show();

        Map<String, Object> params = new HashMap<>(4);
        if (!TextUtils.isEmpty(userId)) {
            params.put("userId", userId);
        }
        params.put("channelKey", channelKey);
        params.put("channelType", "5");
        params.put("client_id", "ms_apis");
        params.put("client_secret", "APHGN0VXP4vynmCXfWqjDrCiLFP1EJez");
        params.put("grant_type", "password");
        params.put("signMode", "5");

        mOkHttpHelper.post(mConfigUtils.getLoginUrl(), params, new SimpleCallBack<LoginEntity>(MainActivity.this) {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "onFailure: request = " + request + " e = " + e);
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }

            @Override
            public void onSuccess(Response response, LoginEntity loginEntity) {
                Log.d(TAG, "onSuccess: response = " + response + " loginEntity = " + loginEntity);
                if (loginEntity != null && "E_000".equals(loginEntity.getCode()) && loginEntity.getData() != null) {
                    Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

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

                        AgentWebConfig.syncCookie(getUrl(), "sdk_key=" + "SDKTest");
                        AgentWebConfig.syncCookie(getUrl(), "sdk_token=" + loginEntity.getData().getToken());
                        AgentWebConfig.syncCookie(getUrl(), "sdk_refreshToken=" + loginEntity.getData().getRefreshToken());
                        AgentWebConfig.syncCookie(getUrl(), "sdk_userBody=" + new Gson().toJson(loginEntity.getData().getBody()));

                        String tag = AgentWebConfig.getCookiesByUrl(targetUrl);
                        Log.i(TAG, "tag:" + tag);

                        gotoWebSDK();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "登录失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                loadingPopup.dismiss();
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                Log.e(TAG, "onError: response" + response + " code = " + code + " e = " + e);
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }

            @Override
            public void onTokenError(Response response, int code) {
                Log.e(TAG, "onTokenError: response" + response + " code = " + code);
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }
        });
    }

    private void gotoWebSDK() {
        startActivity(new Intent(MainActivity.this, CommonActivity.class));
    }
    // -------------------- 登录相关 end --------------------

}