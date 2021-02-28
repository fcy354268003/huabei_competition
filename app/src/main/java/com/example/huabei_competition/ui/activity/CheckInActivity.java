package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.example.huabei_competition.R;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.snackbar.Snackbar;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;


/**
 * Create by FanChenYang
 */
public class CheckInActivity extends BaseActivity implements LogIn.LogCallback {

    private EditText mPassword;
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_);
        WidgetUtil.setCustomerText(findViewById(R.id.tv_title), WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        animation();
    }

    private static final String TAG = "CheckInActivity";

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPassword = findViewById(R.id.et_password);
        mUserName = findViewById(R.id.et_userName);
        Drawable drawable1 = getResources().getDrawable(R.drawable.user2);
        drawable1.setBounds(0, 0, 30, 0);
        mPassword.setCompoundDrawables(drawable1, null, null, null);
        Drawable drawable2 = getResources().getDrawable(R.drawable.password);
        drawable2.setBounds(0, 0, 30, 0);
        mUserName.setCompoundDrawables(drawable2, null, null, null);
    }

    /**
     * 开启动画
     */
    private void animation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_check);
        findViewById(R.id.btn_log).startAnimation(animation);
        findViewById(R.id.l1).startAnimation(animation);
        findViewById(R.id.l2).startAnimation(animation);
        findViewById(R.id.btn_register).startAnimation(animation);
        findViewById(R.id.findPassword).startAnimation(animation);
    }

    @Override
    public void startLoading() {
        super.startLoading();
        findViewById(R.id.l2).setVisibility(View.GONE);
        findViewById(R.id.l1).setVisibility(View.GONE);
        findViewById(R.id.btn_log).setVisibility(View.GONE);
        findViewById(R.id.linearLayout2).setVisibility(View.GONE);
    }

    @Override
    protected void stopLoading() {
        super.stopLoading();
        findViewById(R.id.l2).setVisibility(View.VISIBLE);
        findViewById(R.id.l1).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_log).setVisibility(View.VISIBLE);
        findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
    }

    @Override
    public void success(String name, String pass) {
        JMessageClient.login(name, pass, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    UserUtil.sUserName = name;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    failure();
                }
            }
        });
    }

    @Override
    public void failure() {
        Snackbar.make(mPassword, "登陆失败", Snackbar.LENGTH_LONG).show();
    }

    /**
     * @param view 登录按钮
     */
    public void log(View view) {
        UserUtil.logIn(this, mUserName.getText().toString(), mPassword.getText().toString(), this);
    }

    /**
     * @param view 注册按钮
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(CheckInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * @param view 找回密码按钮
     */
    public void goToForget(View view) {

    }
}