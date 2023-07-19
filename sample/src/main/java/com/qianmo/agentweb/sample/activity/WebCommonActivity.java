package com.qianmo.agentweb.sample.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.qianmo.agentweb.MSToken;
import com.qianmo.agentweb.bridge.JsBridgeListener;
import com.qianmo.agentweb.bridge.JsCallbackResponse;
import com.qianmo.agentweb.bridge.JsInteractType;
import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.sample.net.LoginEntity;
import com.qianmo.agentweb.sample.net.OkHttpHelper;
import com.qianmo.agentweb.sample.net.SimpleCallBack;
import com.qianmo.agentweb.sample.utils.ConfigUtils;
import com.qianmo.agentwebX5.MSkitWebX5;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangxianghui on 2017/5/23.
 */
public class WebCommonActivity extends AppCompatActivity {

    private static final String TAG = WebCommonActivity.class.getSimpleName();

    private ImageView mBackImageView;
    private View mLineView;
    private ImageView mFinishImageView;
    protected TextView mTitleTextView;
    private ImageView mMoreImageView;
    private PopupMenu mPopupMenu;

    private MSkitWebX5 mSkitWeb;

    private ConfigUtils mConfigUtils;

    private OkHttpHelper mOkHttpHelper;

    private LoadingPopupView loadingPopup;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_common);

        initView();
        LinearLayout container = findViewById(R.id.container_framelayout);

        mSkitWeb = MSkitWebX5.with(this)
                .setAgentWebParent(container, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .setJsBridgeListener(new MyJsBridgeListener())
                .loadUrl(ConfigUtils.getInstance(this).getSDKUrl());

        mConfigUtils = ConfigUtils.getInstance(this);
        mOkHttpHelper = OkHttpHelper.getInstance();
    }

    protected void initView() {
        mBackImageView = (ImageView) findViewById(R.id.iv_back);
        mLineView = findViewById(R.id.view_line);
        mFinishImageView = (ImageView) findViewById(R.id.iv_finish);
        mTitleTextView = (TextView) findViewById(R.id.toolbar_title);
        mMoreImageView = (ImageView) findViewById(R.id.iv_more);

        mFinishImageView.setOnClickListener(mOnClickListener);
        mBackImageView.setOnClickListener(mOnClickListener);
        mMoreImageView.setOnClickListener(mOnClickListener);
    }

    @Override
    public void onResume() {
        mSkitWeb.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mSkitWeb.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mSkitWeb.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mSkitWeb != null) {
            if (mSkitWeb.handleKeyEvent(keyCode, event)) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    // true表示AgentWeb处理了该事件
                    if (!mSkitWeb.back()) {
                        finish();
                    }
                    break;
                case R.id.iv_finish:
                    finish();
                    break;
                case R.id.iv_more:
                    showPoPup(v);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示更多菜单
     *
     * @param view 菜单依附在该View下面
     */
    private void showPoPup(View view) {
        if (mPopupMenu == null) {
            mPopupMenu = new PopupMenu(this, view);
            mPopupMenu.inflate(R.menu.toolbar_menu);
            mPopupMenu.setOnMenuItemClickListener(mOnMenuItemClickListener);
        }
        mPopupMenu.show();
    }

    /**
     * 菜单事件
     */
    private final PopupMenu.OnMenuItemClickListener mOnMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.refresh:
                    if (mSkitWeb != null) {
                        mSkitWeb.reload(); // 刷新
                    }
                    return true;
                case R.id.default_clean:
                    if (mSkitWeb != null) {
                        mSkitWeb.syncNot();
                    }
                    return true;
                default:
                    return false;
            }
        }
    };

    private class MyJsBridgeListener implements JsBridgeListener {
        @Override
        public void onJsInteract(int actionType, JsCallbackResponse response) {
            switch (actionType) {
                case JsInteractType.LOGIN_FROM_WEB:
                    Toast.makeText(WebCommonActivity.this, "登录成功: " + response, Toast.LENGTH_SHORT).show();
                    break;
                case JsInteractType.LOGOUT_FROM_WEB:
                    mSkitWeb.syncNot();
                    Toast.makeText(WebCommonActivity.this, "登出成功: " + response, Toast.LENGTH_SHORT).show();
                    break;
                case JsInteractType.LOGIN_BY_APP:
                    Toast.makeText(WebCommonActivity.this, "登录 token 已失效，请重新登录: " + response, Toast.LENGTH_SHORT).show();
                    showInfoInputDialog();
                    break;
                case JsInteractType.TITLE_RECEIVE:
                    String title = response.getResponseData();
                    if (!TextUtils.isEmpty(title)) {
                        if (title.length() > 10) {
                            title = title.substring(0, 10).concat("...");
                        }
                        if (mTitleTextView != null) mTitleTextView.setText(title);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    // -------------------- 登录相关 start --------------------
    private void showInfoInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_input_info, null);
        final EditText etUserId = view.findViewById(R.id.et_user_id);

        etUserId.setText("UserIdTest");

        builder.setTitle("请输入以下信息");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                doLogin(etUserId.getText().toString().trim());

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

    private void doLogin(String userId) {
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
        params.put("channelKey", mConfigUtils.getSDKChannelKey());
        params.put("channelType", "5");
        params.put("client_id", "ms_apis");
        params.put("client_secret", "APHGN0VXP4vynmCXfWqjDrCiLFP1EJez");
        params.put("grant_type", "password");
        params.put("signMode", "5");

        mOkHttpHelper.post(mConfigUtils.getLoginUrl(), params, new SimpleCallBack<LoginEntity>(this) {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "onFailure: request = " + request + " e = " + e);
                Toast.makeText(WebCommonActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }

            @Override
            public void onSuccess(Response response, LoginEntity loginEntity) {
                Log.d(TAG, "onSuccess: response = " + response + " loginEntity = " + loginEntity);
                if (loginEntity != null && "E_000".equals(loginEntity.getCode()) && loginEntity.getData() != null) {
                    Toast.makeText(WebCommonActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                    MSToken msToken = new MSToken();
                    msToken.setToken(loginEntity.getData().getToken());
                    msToken.setRefreshToken(loginEntity.getData().getRefreshToken());

                    LoginEntity.BodyBean bodyBean = loginEntity.getData().getBody();

                    MSToken.BodyBean msBodyBean = new MSToken.BodyBean();
                    msBodyBean.setIsBind(bodyBean.getIsBind());
                    msBodyBean.setIsNew(bodyBean.getIsNew());
                    msBodyBean.setSignMode(bodyBean.getSignMode());

                    msToken.setBody(msBodyBean);

                    mSkitWeb.sync(msToken);
                } else {
                    Toast.makeText(WebCommonActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                loadingPopup.dismiss();
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                Log.e(TAG, "onError: response" + response + " code = " + code + " e = " + e);
                Toast.makeText(WebCommonActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }

            @Override
            public void onTokenError(Response response, int code) {
                Log.e(TAG, "onTokenError: response" + response + " code = " + code);
                Toast.makeText(WebCommonActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }
        });
    }
    // -------------------- 登录相关 end --------------------

}
