package com.example.huabei_competition.ui.findpass;


import androidx.annotation.Nullable;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentForgetPass1Binding;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.PasswordRel;
import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ForgetPassActivity extends BaseActivity implements Callback {
    private Gson gson;
    private FragmentForgetPass1Binding binding;
    private int state = 0;
    private MyHandler handler = MyHandler.obtain(this, null);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_forget_pass_1);
        binding.setLifecycleOwner(this);
        binding.ivBack.setOnClickListener(this::back);
        binding.btnConfirm.setOnClickListener(this::sure);
        WidgetUtil.setCustomerText(binding.tvForget, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        findViewById(R.id.sendVer).findViewById(R.id.button2).setOnClickListener(this::sendVer);
    }

    private void sendVer(View view) {
        view.setClickable(false);
        view.setBackgroundColor(Color.GRAY);
        MyToast.showMessage("消息已发送");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
            }
        }, 1000 * 60);
        PasswordRel.forget_2(token, this);
    }

    private static final String TAG = "ForgetPassActivity";

    private void sure(View view) {
        Log.d(TAG, "sure: ");
        switch (state) {
            case 0:
                String s = binding.etUserOrPhone.getText().toString();
                if (TextUtils.isEmpty(s)) {
                    MyToast.showMessage("请正确输入");
                    return;
                }
                PasswordRel.forget_1(s, this);
                Log.d(TAG, "sure:  " + s + state);
                break;
            case 2:
                EditText editText = binding.sendVer.findViewById(R.id.verification);
                String ver = editText.getText().toString();
                PasswordRel.forget_3(token, ver, this);
                Log.d(TAG, "sure: " + state);
                break;
            case 3:
                String one = binding.etOne.getText().toString();
                String two = binding.etTwo.getText().toString();
                if (TextUtils.isEmpty(one) || TextUtils.isEmpty(two)) {
                    MyToast.showMessage("请填写完整");
                    return;
                }
                if (!TextUtils.equals(one, two)) {
                    MyToast.showMessage("两次填写密码不一致");
                    return;
                }
                Log.d(TAG, "sure: " + state);
                PasswordRel.forget_4(token, one, this);
                break;
        }
        binding.btnConfirm.setClickable(false);
    }

    private void back(View view) {
        finish();
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                MyToast.showMessage("网络异常");
                binding.btnConfirm.setClickable(true);
            }
        });
    }

    private String token;
    private String phone;

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        binding.btnConfirm.setClickable(true);
        if (response.isSuccessful()) {
            switch (state) {
                case 0:
                    PasswordRel.Forget_1_1 forget_1_1 = gson.fromJson(response.body().string(), PasswordRel.Forget_1_1.class);
                    if (forget_1_1.getCode().equals(LogIn.OK)) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                state++;
                                binding.cvUserNameOrPhone.setVisibility(View.GONE);
                                phone = forget_1_1.getData().getPhone();
                                token = forget_1_1.getData().getToken();
                                findViewById(R.id.sendVer).setVisibility(View.VISIBLE);
                                TextView phoneP = findViewById(R.id.sendVer).findViewById(R.id.tv_phone);
                                phoneP.setText(phoneP.getText().toString() + phone);
                            }
                        });
                    }
                    break;
                case 1:
                    state++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showMessage("已成功发送验证码");
                        }
                    });
                    break;
                case 2:
                    PasswordRel.Forget_3_3 forget_3_3 = gson.fromJson(response.body().string(), PasswordRel.Forget_3_3.class);
                    if (forget_3_3.getCode().equals(LogIn.OK)) {
                        state++;
                        token = forget_3_3.getData().getToken();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.sendVer).setVisibility(View.GONE);
                                findViewById(R.id.cv_one_4).setVisibility(View.VISIBLE);
                                findViewById(R.id.cv_two_4).setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    break;
                case 3:
                    PasswordRel.Forget_4_4 forget_4_4 = gson.fromJson(response.body().string(), PasswordRel.Forget_4_4.class);
                    if (forget_4_4.getCode().equals(LogIn.OK)) {
                        Snackbar.make(binding.getRoot(), "修改成功", Snackbar.LENGTH_SHORT).show();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 500);
                    }
                    break;
            }
        }
        response.close();
    }
}