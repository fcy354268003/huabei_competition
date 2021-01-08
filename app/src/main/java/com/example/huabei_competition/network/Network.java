package com.example.huabei_competition.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Create by FanChenYang at 2020/12/15
 * 目的：
 * 对Retrofit进行简单封装
 */
public class Network {
    private static OkHttpClient mClient;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    /**
     * @param baseUrl baseUrl
     * @param service 接口类
     * @param <T>     接口泛型
     * @return 接口实现
     */
    public static <T> T getCall(String baseUrl, Class<T> service) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(service);
    }

    /**
     * @param context 上下文
     * @return 网络是否可用
     */
    public static boolean checkNetWorkState(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 当网络没有链接的时候的时候 activeNetworkInfo 为空，当其不为空的也存在网络不可用的情况
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static final String TAG = "Network";

    /**
     * @return client
     */
    public static OkHttpClient getClient() {
        if (mClient == null) {
            mClient = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        @Override
                        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                            cookieStore.put(httpUrl.host(), list);
                        }

                        @NotNull
                        @Override
                        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                            List<Cookie> cookies = cookieStore.get(httpUrl.host());
                            return cookies != null ? cookies : new ArrayList<>();
                        }
                    })
                    .build();
        }
        return mClient;
    }

}
