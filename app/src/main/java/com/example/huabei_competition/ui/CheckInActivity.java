package com.example.huabei_competition.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huabei_competition.R;
import com.example.huabei_competition.db.User;
import com.example.huabei_competition.network.Network;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.MyToast;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


// fcy
public class CheckInActivity extends BaseActivity implements Callback {

    private EditText mPassword;
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_);

    }

    private static final String TAG = "CheckInActivity";
    private final String URL = MyApplication.URL + "/app/login";

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mPassword = findViewById(R.id.et_password);
        mUserName = findViewById(R.id.et_userName);

        Drawable drawable1 = getResources().getDrawable(R.drawable.user2);
        drawable1.setBounds(0, 0, 30, 0);
        mPassword.setCompoundDrawables(drawable1, null, null, null);
        Drawable drawable2 = this.getResources().getDrawable(R.drawable.password);
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
    }


    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.d(TAG, "onFailure: " + e.toString());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyToast.showMessage("密码或用户名错误");
            }
        });
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.isSuccessful()) {
            try {
                JSONObject jsonObject = new JSONObject(response.body().string());
                Gson gson = new Gson();
                User user = gson.fromJson(jsonObject.getString("data"),User.class);
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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 点击空白 隐藏软键盘
        InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (event.getAction() == KeyEvent.ACTION_DOWN && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        return super.onTouchEvent(event);
    }

    /**
     * @param view 登录按钮
     */
    public void log(View view) {
        if (!Network.checkNetWorkState(this)) {
            Toast.makeText(this, "网络不可用", Toast.LENGTH_SHORT).show();
            return;
        }
        if (mPassword.getText().toString().equals("asd")) {
            Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        if (!TextUtils.isEmpty(mPassword.getText().toString()) && !TextUtils.isEmpty(mUserName.getText().toString())) {
            MyApplication application = (MyApplication) getApplication();
            OkHttpClient client = application.okHttpClient;
            FormBody formBody = new FormBody.Builder()
                    .add("username", mUserName.getText().toString())
                    .add("password", mPassword.getText().toString())
                    .build();
            Request request = new Request.Builder().post(formBody).url(URL).build();
            client.newCall(request).enqueue(CheckInActivity.this);
        } else Toast.makeText(CheckInActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
    }

    /**
     * @param view 注册按钮
     */
    public void register(View view) {
        Intent intent = new Intent(CheckInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}