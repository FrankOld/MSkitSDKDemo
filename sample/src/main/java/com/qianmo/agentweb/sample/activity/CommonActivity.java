package com.qianmo.agentweb.sample.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.sample.common.FragmentKeyDown;
import com.qianmo.agentweb.sample.fragment.AgentWebFragment;
import com.qianmo.agentweb.sample.utils.ConfigUtils;

/**
 * Created by wangxianghui on 2017/5/23.
 */
public class CommonActivity extends AppCompatActivity {

    private AgentWebFragment mAgentWebFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common);

        openFragment();
    }

    private void openFragment() {
        FragmentManager mFragmentManager = this.getSupportFragmentManager();
        FragmentTransaction ft = mFragmentManager.beginTransaction();

        Bundle mBundle = new Bundle();
        mBundle.putString(AgentWebFragment.URL_KEY, ConfigUtils.getInstance(this).getSDKUrl());

        ft.add(R.id.container_framelayout, mAgentWebFragment = AgentWebFragment.getInstance(mBundle), AgentWebFragment.class.getName());
        ft.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AgentWebFragment mAgentWebFragment = this.mAgentWebFragment;
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
