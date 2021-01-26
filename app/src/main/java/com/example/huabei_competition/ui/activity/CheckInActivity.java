package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;


import com.example.huabei_competition.R;
import com.example.huabei_competition.db.User;
import com.example.huabei_competition.event.EventReceiver;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.UserUtil;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
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
        WidgetUtil.setCustomerText(findViewById(R.id.tv_title), WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        findViewById(R.id.findPassword).setOnClickListener(veiw -> {
            EventReceiver.getInstance().test();
        });
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
                // 极光相关登录
                SharedPreferences jgim = getSharedPreferences(JG_SHARED_NAME, MODE_PRIVATE);
                boolean isRegisted = jgim.getBoolean(IS_REGISTED, false);
                //没有注册
                if (!isRegisted) {
                    // 先注册
                    JGRegister(jgim);
                }
                JGLogin();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static final String JG_SHARED_NAME = "JGIM";
    public static final String IS_REGISTED = "isRegistered";

    /**
     * 极光注册
     */
    private void JGRegister(SharedPreferences sharedPreferences) {
        String password = mPassword.getText().toString();
        String userName = mUserName.getText().toString();
        JMessageClient.register(userName, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: " + s);
            }
        });
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(IS_REGISTED, true);
        edit.apply();
    }

    /**
     * 极光登录
     */
    private void JGLogin() {
        String password = mPassword.getText().toString();
        String userName = mUserName.getText().toString();
        JMessageClient.login(userName, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: " + s);
            }
        });

    }

}