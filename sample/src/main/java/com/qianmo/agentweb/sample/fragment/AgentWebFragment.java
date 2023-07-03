package com.qianmo.agentweb.sample.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.qianmo.agentweb.MSkitWeb;
import com.qianmo.agentweb.bridge.JsBridgeListener;
import com.qianmo.agentweb.bridge.JsCallbackResponse;
import com.qianmo.agentweb.bridge.JsInteractType;
import com.qianmo.agentweb.core.AgentWebConfig;
import com.qianmo.agentweb.sample.R;
import com.qianmo.agentweb.sample.common.FragmentKeyDown;
import com.qianmo.agentweb.sample.net.LoginEntity;
import com.qianmo.agentweb.sample.net.OkHttpHelper;
import com.qianmo.agentweb.sample.net.SimpleCallBack;
import com.qianmo.agentweb.sample.utils.ConfigUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wangxianghui on 2017/5/15.
 */
public class AgentWebFragment extends Fragment implements FragmentKeyDown {

    public static final String TAG = AgentWebFragment.class.getSimpleName();
    public static final String URL_KEY = "url_key";

    private ImageView mBackImageView;
    private View mLineView;
    private ImageView mFinishImageView;
    protected TextView mTitleTextView;
    private ImageView mMoreImageView;
    private PopupMenu mPopupMenu;

    private MSkitWeb mSkitWeb;

    private ConfigUtils mConfigUtils;

    private OkHttpHelper mOkHttpHelper;

    private LoadingPopupView loadingPopup;

    public static AgentWebFragment getInstance(Bundle bundle) {
        AgentWebFragment mAgentWebFragment = new AgentWebFragment();
        if (bundle != null) {
            mAgentWebFragment.setArguments(bundle);
        }

        return mAgentWebFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_agentweb, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSkitWeb = MSkitWeb.with(this)
                .setAgentWebParent((ViewGroup) view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .setJsBridgeListener(new MyJsBridgeListener())
                .loadUrl(getUrl());

        initView(view);

        mConfigUtils = ConfigUtils.getInstance(this.getContext());
        mOkHttpHelper = OkHttpHelper.getInstance();
    }

    protected void initView(View view) {
        mBackImageView = (ImageView) view.findViewById(R.id.iv_back);
        mLineView = view.findViewById(R.id.view_line);
        mFinishImageView = (ImageView) view.findViewById(R.id.iv_finish);
        mTitleTextView = (TextView) view.findViewById(R.id.toolbar_title);
        mBackImageView.setOnClickListener(mOnClickListener);
        mFinishImageView.setOnClickListener(mOnClickListener);
        mMoreImageView = (ImageView) view.findViewById(R.id.iv_more);
        mMoreImageView.setOnClickListener(mOnClickListener);
    }

    private class MyJsBridgeListener implements JsBridgeListener {
        @Override
        public void onJsInteract(int actionType, JsCallbackResponse response) {
            switch (actionType) {
                case JsInteractType.LOGIN_FROM_WEB:
                    Toast.makeText(getActivity(), "登录成功: " + response, Toast.LENGTH_SHORT).show();
                    break;
                case JsInteractType.LOGOUT_FROM_WEB:
                    toCleanWebCache();
                    Toast.makeText(getActivity(), "登出成功: " + response, Toast.LENGTH_SHORT).show();
                    break;
                case JsInteractType.LOGIN_BY_APP:
                    Toast.makeText(getActivity(), "登录 token 已失效，请重新登录: " + response, Toast.LENGTH_SHORT).show();
                    showInfoInputDialog();
                    break;
                case JsInteractType.TITLE_RECEIVE:
                    String title = response.getResponseData();
                    if (mTitleTextView != null && !TextUtils.isEmpty(title)) {
                        if (title.length() > 10) {
                            title = title.substring(0, 10).concat("...");
                        }
                        mTitleTextView.setText(title);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 页面空白，请检查scheme是否加上， scheme://host:port/path?query&query 。
     *
     * @return mUrl
     */
    public String getUrl() {
        String target = "";

        if (TextUtils.isEmpty(target = this.getArguments().getString(URL_KEY))) {
            target = ConfigUtils.getInstance(getContext()).getSDKUrl();
        }

        return target;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_back:
                    // true表示AgentWeb处理了该事件
                    if (!mSkitWeb.back()) {
                        AgentWebFragment.this.getActivity().finish();
                    }
                    break;
                case R.id.iv_finish:
                    AgentWebFragment.this.getActivity().finish();
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
            mPopupMenu = new PopupMenu(this.getActivity(), view);
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
                case R.id.copy:
                    if (mSkitWeb != null) {
                        toCopy(AgentWebFragment.this.getContext(), mSkitWeb.getUrl());
                    }
                    return true;
                case R.id.default_browser:
                    if (mSkitWeb != null) {
                        openBrowser(mSkitWeb.getUrl());
                    }
                    return true;
                case R.id.default_clean:
                    toCleanWebCache();
                    return true;
                default:
                    return false;
            }
        }
    };

    /**
     * 清除 WebView 缓存
     */
    private void toCleanWebCache() {
        if (this.mSkitWeb != null) {
            //清理所有跟WebView相关的缓存 ，数据库， 历史记录 等。
            this.mSkitWeb.clearWebCache();
            Toast.makeText(getActivity(), "已清理缓存", Toast.LENGTH_SHORT).show();
            //清空所有 AgentWeb 硬盘缓存，包括 WebView 的缓存 , AgentWeb 下载的图片 ，视频 ，apk 等文件。
            AgentWebConfig.clearDiskCache(this.getContext());
        }
    }

    /**
     * 打开浏览器
     *
     * @param targetUrl 外部浏览器打开的地址
     */
    private void openBrowser(String targetUrl) {
        if (TextUtils.isEmpty(targetUrl) || targetUrl.startsWith("file://")) {
            Toast.makeText(this.getContext(), targetUrl + " 该链接无法使用浏览器打开。", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri mUri = Uri.parse(targetUrl);
        intent.setData(mUri);
        startActivity(intent);
    }

    /**
     * 复制字符串
     *
     * @param context
     * @param text
     */
    private void toCopy(Context context, String text) {
        ClipboardManager mClipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        mClipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
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
    public boolean onFragmentKeyDown(int keyCode, KeyEvent event) {
        return mSkitWeb.handleKeyEvent(keyCode, event);
    }

    @Override
    public void onDestroyView() {
        mSkitWeb.onDestroy();
        super.onDestroyView();
    }

    private void showInfoInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_input_info, null);
        final EditText etSdkKey = view.findViewById(R.id.et_shop_url);
        final EditText etUserId = view.findViewById(R.id.et_user_id);

        etSdkKey.setText("SDKTest");
        etUserId.setText("UserIdTest");

        builder.setTitle("请输入以下信息");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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

    // -------------------- 登录相关 start --------------------
    private void doLogin(String channelKey, String userId) {
        if (loadingPopup == null) {
            loadingPopup = new XPopup.Builder(this.getContext())
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

        mOkHttpHelper.post(mConfigUtils.getLoginUrl(), params, new SimpleCallBack<LoginEntity>(getContext()) {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "onFailure: request = " + request + " e = " + e);
                Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }

            @Override
            public void onSuccess(Response response, LoginEntity loginEntity) {
                Log.d(TAG, "onSuccess: response = " + response + " loginEntity = " + loginEntity);
                if (loginEntity != null && "E_000".equals(loginEntity.getCode()) && loginEntity.getData() != null) {
                    Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();

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

                        // 保存登录信息
                        AgentWebConfig.syncCookie(getUrl(), "sdk_key=" + "SDKTest");  // TODO 替换自己对应的渠道key
                        AgentWebConfig.syncCookie(getUrl(), "sdk_token=" + loginEntity.getData().getToken());
                        AgentWebConfig.syncCookie(getUrl(), "sdk_refreshToken=" + loginEntity.getData().getRefreshToken());
                        AgentWebConfig.syncCookie(getUrl(), "sdk_userBody=" + new Gson().toJson(loginEntity.getData().getBody()));

                        String tag = AgentWebConfig.getCookiesByUrl(targetUrl);
                        Log.i(TAG, "tag:" + tag);

                        if (mSkitWeb != null) {
                            mSkitWeb.reload(); // 刷新
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "登录失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
                }
                loadingPopup.dismiss();
            }

            @Override
            public void onError(Response response, int code, Exception e) {
                Log.e(TAG, "onError: response" + response + " code = " + code + " e = " + e);
                Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }

            @Override
            public void onTokenError(Response response, int code) {
                Log.e(TAG, "onTokenError: response" + response + " code = " + code);
                Toast.makeText(getContext(), "登录失败", Toast.LENGTH_SHORT).show();
                loadingPopup.dismiss();
            }
        });
    }
    // -------------------- 登录相关 end --------------------

}
