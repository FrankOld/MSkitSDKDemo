package com.qianmo.agentweb.sample.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.sample.common.FragmentKeyDown;
import com.qianmo.agentweb.sample.fragment.WebFragment;
import com.qianmo.agentweb.sample.utils.ConfigUtils;

/**
 * Created by wangxianghui on 2017/5/23.
 */
public class WebFragmentActivity extends AppCompatActivity {

    private WebFragment mAgentWebFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_fragment);

        openFragment();
    }

    private void openFragment() {
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        Bundle mBundle = new Bundle();
        mBundle.putString(WebFragment.URL_KEY, ConfigUtils.getInstance(this).getSDKUrl());

        ft.add(R.id.container_framelayout, mAgentWebFragment = WebFragment.getInstance(mBundle), WebFragment.class.getName());
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebFragment mAgentWebFragment = this.mAgentWebFragment;
        if (mAgentWebFragment != null) {
            if (((FragmentKeyDown) mAgentWebFragment).onFragmentKeyDown(keyCode, event)) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);
    }

}
