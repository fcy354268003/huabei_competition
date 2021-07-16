package com.example.huabei_competition.network.api;

import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.NonNull;

import com.example.huabei_competition.event.ChatRoomUtil;

import com.example.huabei_competition.network.base.NetRequest;
import com.example.huabei_competition.network.base.NetRequestCallback;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/27
 * 49.232.223.89:10001
 */

public class LogIn {
    public static final String PATH_LOGIN = "/login";
    public static String TOKEN;
    private static final String TAG = "LogIn";
    private static final Gson gson = new Gson();
    public static final String BASIC_PATH = "http://49.232.223.89:10001";

    public static void login(@NonNull LogCallback callback, @NonNull final String name, @NonNull final String pass) {
        HashMap<String, String> map = new HashMap<>();
        map.put("username", name);
        map.put("password", pass);
        NetRequest.postFromRequest(Register.toIntactUrl(PATH_LOGIN), map, new NetRequestCallback() {
            @Override
            public void success(@Nullable String result) {
                Log.d(TAG, "success: " + result);
                LogResponse logResponse = gson.fromJson(result, LogResponse.class);
                if (TextUtils.equals(logResponse.code, OK)) {
                    TOKEN = logResponse.data.token;
                    NetRequest.DefaultInterceptor.token = TOKEN;
                    callback.success(name, pass);
                } else {
                    callback.failure();
                }
                callback.success(name, pass);
            }

            @Override
            public void failure(@NotNull Request request, @NotNull Exception exception) {
                callback.failure();
            }
        });
    }

    public interface LogCallback {
        void success(String name, String pass);

        void failure();
    }

    public static final String OK = "0000";


    public static class LogResponse {
        String code;
        String message;
        Data data;

        static class Data {
            String token;
        }
    }
}
