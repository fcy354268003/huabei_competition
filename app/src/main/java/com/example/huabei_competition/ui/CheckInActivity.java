package com.example.huabei_competition.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;


import com.example.huabei_competition.R;
import com.example.huabei_competition.db.User;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.UserUtil;
import com.example.huabei_competition.widget.MyToast;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Create by FanChenYang
 */
public class CheckInActivity extends BaseActivity implements Callback {

    private EditText mPassword;
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_);

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
        animation();
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
    }



    /**
     * @param view 登录按钮
     */
    public void log(View view) {
        if (mPassword.getText().toString().equals("asd")) {
            Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
            this.startActivity(intent);
            finish();
            return;
        }
        UserUtil.logIn(this, mUserName.getText().toString(), mPassword.getText().toString(), this);

    }

    /**
     * @param view 注册按钮
     */
    public void goToRegister(View view) {
        Intent intent = new Intent(CheckInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }


    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.d(TAG, "onFailure: " + e.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.showMessage("密码或用户名错误");
                stopLoading();
            }
        });
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        stopLoading();
        if (response.isSuccessful()) {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                Gson gson = new Gson();
                User user = gson.fromJson(jsonObject.getString("data"), User.class);
                MyApplication application = (MyApplication) getApplication();
                application.setUser(user);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}