package com.qianmo.agentweb.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.qianmo.agentweb.MSkitWeb;
import com.qianmo.agentweb.core.AgentWebConfig;
import com.qianmo.agentweb.sample.BuildConfig;
import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.utils.SPUtils;

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
                logout();
            }
        });

        findViewById(R.id.web_new_sdk_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebCommonActivity.class));
            }
        });
        findViewById(R.id.web_new_sdk_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoWebSDK();
            }
        });

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        if ((Boolean) SPUtils.getData("access_mode_default", true)) {
            radioGroup.check(R.id.default1);
            SPUtils.putData(SPUtils.KEY_SDK_ACCESS_MODE, MSkitWeb.SdkAccessMode.DEFAULT.value);
        } else {
            radioGroup.check(R.id.own);
            SPUtils.putData(SPUtils.KEY_SDK_ACCESS_MODE, MSkitWeb.SdkAccessMode.OWN.value);
        }
        SPUtils.getInstance(this, SPUtils.FILE_NAME);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.default1) {
                    SPUtils.putData("access_mode_default", true);
                    SPUtils.putData(SPUtils.KEY_SDK_ACCESS_MODE, MSkitWeb.SdkAccessMode.DEFAULT.value);
                } else {
                    SPUtils.putData("access_mode_default", false);
                    SPUtils.putData(SPUtils.KEY_SDK_ACCESS_MODE, MSkitWeb.SdkAccessMode.OWN.value);
                }
            }
        });

        ((TextView) findViewById(R.id.tv_current_sdk_version)).setText(
                "当前SDK版本：" + AgentWebConfig.AGENTWEB_VERSION + "\n当前APP版本：" + BuildConfig.VERSION_NAME + "，build：" + BuildConfig.VERSION_CODE
        );

    }

    private void logout() {
        MSkitWeb.userLogout(this);
        Toast.makeText(MainActivity.this, "已登出", Toast.LENGTH_SHORT).show();
    }

    private void gotoWebSDK() {
        startActivity(new Intent(MainActivity.this, WebFragmentActivity.class));
    }

}