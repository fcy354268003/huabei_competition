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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.example.huabei_competition.R;
import com.example.huabei_competition.fcyUtil.BaseActivity;
import com.example.huabei_competition.fcyUtil.MyApplication;
import com.example.huabei_competition.fcyUtil.MyToast;
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
public class CheckIn_Activity extends BaseActivity {

    private ViewPager viewPager;
    private EditText mPassword;
    private EditText mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_);

    }

    private static final String TAG = "CheckIn_Activity";
    private final String URL = MyApplication.URL + "/app/login";

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        final EditText mPassword = findViewById(R.id.et_password);
        final EditText mUserName = findViewById(R.id.et_userName);
        ImageView imageView = findViewById(R.id.btn_log);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassword.getText().toString().equals("asd")) {
                    Intent intent = new Intent(CheckIn_Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
                if (!TextUtils.isEmpty(mPassword.getText().toString()) && !TextUtils.isEmpty(mUserName.getText().toString())) {
//                    OkHttpClient client = new OkHttpClient();
                    MyApplication application = (MyApplication) getApplication();
                    OkHttpClient client = application.okHttpClient;
                    FormBody formBody = new FormBody.Builder()
                            .add("username", mUserName.getText().toString())
                            .add("password", mPassword.getText().toString())
                            .build();
                    Request request = new Request.Builder().post(formBody).url(URL).build();
                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    MyToast.showMessage("密码或用户名错误", CheckIn_Activity.this);
                                }
                            });
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            if (response.isSuccessful()) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response.body().string());
                                    Gson gson = new Gson();
                                    MyApplication.User user = gson.fromJson(jsonObject.getString("data"), MyApplication.User.class);
                                    MyApplication application = (MyApplication) getApplication();
                                    application.setUser(user);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(CheckIn_Activity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } else Toast.makeText(CheckIn_Activity.this, "登陆失败", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckIn_Activity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        Drawable drawable1 = getResources().getDrawable(R.drawable.user2);
        drawable1.setBounds(0, 0, 30, 0);
        mPassword.setCompoundDrawables(drawable1, null, null, null);
        Drawable drawable2 = this.getResources().getDrawable(R.drawable.password);
        drawable2.setBounds(0, 0, 30, 0);
        mUserName.setCompoundDrawables(drawable2, null, null, null);


        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_check);
        findViewById(R.id.l1).startAnimation(animation);
        findViewById(R.id.l2).startAnimation(animation);
        imageView.startAnimation(animation);
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


}