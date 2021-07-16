package com.example.huabei_competition.ui.login;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import androidx.annotation.NonNull;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;

import androidx.lifecycle.LifecycleOwner;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.Prop;
import com.example.huabei_competition.db.ShopRole;
import com.example.huabei_competition.network.api.LogIn;

import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.event.UserUtil;


import com.example.huabei_competition.ui.findpass.ForgetPassActivity;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.ui.register.RegisterActivity;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;

import java.util.List;
import java.util.Objects;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;


/**
 * Create by FanChenYang
 */
public class CheckInActivity extends BaseActivity implements LogIn.LogCallback, LifecycleEventObserver {

    private EditText mPassword;
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_);
        WidgetUtil.setCustomerText(findViewById(R.id.tv_title), WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        animation();
        getLifecycle().addObserver(this);
//        showData();
    }

    private void showData() {
        List<Prop> all = LitePal.findAll(Prop.class);
        for (Prop prop : all) {
            System.out.println(prop);
        }

        List<ShopRole> pro = LitePal.findAll(ShopRole.class);
        for (ShopRole product : pro) {
            System.out.println(product);
        }
    }


    private static final String TAG = "CheckInActivity";

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPassword = findViewById(R.id.et_password);
        mUserName = findViewById(R.id.et_userName);
        Drawable drawable1 = ContextCompat.getDrawable(this, R.drawable.user2);
        Objects.requireNonNull(drawable1).setBounds(0, 0, 30, 0);
        mPassword.setCompoundDrawables(drawable1, null, null, null);
        Drawable drawable2 = ContextCompat.getDrawable(this, R.drawable.password);
        Objects.requireNonNull(drawable2).setBounds(0, 0, 30, 0);
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
                    Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    MyToast.showMessage("登陆成功");
                } else {
                    failure();
                }
            }
        });
    }

    @Override
    public void failure() {
        Snackbar.make(mPassword, "登陆失败", Snackbar.LENGTH_LONG).show();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopLoading();
            }
        });
    }

    /**
     * @param view 登录按钮
     */
    public void log(View view) {
//                UserUtil.logIn(this, "87654321", "12345678", this);

        UserUtil.logIn(this, mUserName.getText().toString(), mPassword.getText().toString(), this);
        Log.d(TAG, "log: " + mUserName.getText().toString() + mPassword.getText().toString());
//        success("87654321", "12345678");
    }

    /**
     * @param view 注册按钮
     */
    public void goToRegister(View view) {
        view.setClickable(false);
        Intent intent = new Intent(CheckInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.btn_register).setClickable(true);
        findViewById(R.id.findPassword).setClickable(true);
    }

    /**
     * @param view 找回密码按钮
     */
    public void goToForget(View view) {
        view.setClickable(false);
        Intent intent = new Intent(this, ForgetPassActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy:---- ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        Log.d(TAG, "onStateChanged: " + event.name());
    }
}