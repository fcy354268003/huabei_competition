package com.example.huabei_competition.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.ActivityRegist1Binding;
import com.example.huabei_competition.fcyUtil.MyApplication;
import com.example.huabei_competition.fcyUtil.MyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Create by FanChenYang
 */
public class RegisterActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private ActivityRegist1Binding mBinding;
    private String URL = MyApplication.URL + "/app/regist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regist_1);
        mBinding.setMContext(this);
    }

    public void onCancel() {
        MyToast.showMessage("取消注册成功", this);
        finish();
    }

    public void onConfirm() {
        if (TextUtils.isEmpty(mBinding.etUserName.getText().toString()) || TextUtils.isEmpty(mBinding.etPassword.getText().toString())) {
            MyToast.showMessage("请填写符合规范的账户名与密码", this);
            return;
        }
        findViewById(R.id.userName).setVisibility(View.GONE);
        findViewById(R.id.password).setVisibility(View.GONE);
        mBinding.etPassword.setVisibility(View.GONE);
        mBinding.etUserName.setVisibility(View.GONE);
        mBinding.loadAll.setVisibility(View.VISIBLE);
        mBinding.kwiLoad.startMoving();
        mBinding.btnCancel.setVisibility(View.GONE);
        mBinding.btnConfirm.setVisibility(View.GONE);
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("username", mBinding.etUserName.getText().toString())
                .add("password", mBinding.etPassword.getText().toString())
                .build();
        Request request = new Request.Builder().url(URL).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                finish();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MyToast.showMessage("注册失败", getApplicationContext());
                        mBinding.loadAll.setVisibility(View.GONE);
                        mBinding.kwiLoad.stopMoving();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful())
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showMessage("注册成功", getApplicationContext());
                            finish();
                        }
                    });
            }
        });
    }

    private static final String TAG = "RegisterActivity";
}