package com.example.huabei_competition.network.api;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.huabei_competition.event.ChatRoomUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/27
 */
public class Register {
    public static final String PATH_SEND_VERIFICATION = "/register/verification";
    private static final Gson gson = new Gson();
    public static final String PATH_REGISTER = "/register/user";

    public static void sendVerification(@NonNull String phone, @NonNull View v) {
        Phone pho = new Phone(phone);
        String json = gson.toJson(pho);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_REGISTER)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
                Snackbar.make(v, "登陆异常", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.body().string());
            }
        });
    }

    private static final String TAG = "Register";

    public static void register(@NonNull String userName, @NonNull String password, @NonNull String phone, @NonNull String ver,@NonNull View v,@NonNull VerificationCallback callback) {
        Reg reg = new Reg(userName, password, phone, ver);
        String json = gson.toJson(reg);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_REGISTER)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
                Snackbar.make(v, "网络异常", Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    LogIn.LogResponse logResponse = gson.fromJson(response.body().string(), LogIn.LogResponse.class);
                    if (TextUtils.equals(logResponse.code, LogIn.OK)) {
                        callback.success();
                    }
                } else {
                    Log.d(TAG, "onResponse: " + response.body().string());
                    callback.failure();
                }
            }
        });
    }

    private static class Reg {
        /**
         * username : username
         * password : password
         * phone : phone
         * verification : 手机验证码
         */
        private String username;
        private String password;
        private String phone;
        private String verification;

        public Reg(String username, String password, String phone, String verification) {
            this.username = username;
            this.password = password;
            this.phone = phone;
            this.verification = verification;
        }
    }

    private static class Phone {
        String phone;
        public Phone(String phone) {
            this.phone = phone;
        }
    }

    public interface VerificationCallback {
        void failure();

        void success();
    }

}
