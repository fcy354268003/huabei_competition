package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.ShopRole;
import com.example.huabei_competition.network.api.EncryptionTransmission;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.ui.fragments.ForgetPassActivity;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;

import java.util.List;

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
        boolean aa = EncryptionTransmission.test("ewfhojirwhoweiofhweipjf").equals("TWpGVFYxZHdVVTFvV1dwVE1IcHdia0ZQVlhKRk1WTkpla05STkVNMVIydzRTWEJWVHk4eU9XaFpNRWx5Ym5wbVJYaEZZbnBKTVVGeVRYUkJWMkptUlROaQ==");
        Log.d(TAG, "onCreate: ssssssssssss\n" +aa);
        WidgetUtil.setCustomerText(findViewById(R.id.tv_title), WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        animation();
        List<ShopRole> shopRoles = LitePal.where().find(ShopRole.class);
        for (ShopRole shopRole : shopRoles) {
            Log.d(TAG, "" + shopRole.toString());
        }
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
                            MyToast.showMessage("登陆成功");
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
}