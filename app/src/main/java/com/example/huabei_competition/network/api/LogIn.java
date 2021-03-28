package com.example.huabei_competition.network.api;

import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import com.example.huabei_competition.event.ChatRoomUtil;

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

public class LogIn {
    public static final String PATH_LOGIN = "/login";
    public static String TOKEN;
    private static final String TAG = "LogIn";
    private static final Gson gson = new Gson();
    public static final String BASIC_PATH = "http://192.168.115.65:8000";

    public static void login(@NonNull final String name, @NonNull final String pass, @NonNull LogCallback callback) {
        UserLogIn post = new UserLogIn(name, pass);
        String json = gson.toJson(post);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .post(requestBody)
                .url(BASIC_PATH + PATH_LOGIN)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "onFailure: ");
                callback.failure();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    Log.d(TAG, "onResponse: " + string);
                    LogResponse logResponse = gson.fromJson(string, LogResponse.class);
                    if (TextUtils.equals(logResponse.code, OK)) {
                        TOKEN = logResponse.data.token;
                        callback.success(name, pass);
                    } else {
                        callback.failure();
                    }
                }
                response.close();
            }
        });
    }

    public interface LogCallback {
        void success(String name, String pass);

        void failure();
    }

    public static final String OK = "0000";

    private static class UserLogIn {
        String username;
        String password;

        public UserLogIn(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class LogResponse {
        String code;
        String message;
        Data data;

        static class Data {
            String token;
        }
    }
}
