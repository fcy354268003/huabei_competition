package com.example.huabei_competition.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Build;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.huabei_competition.R;

import com.example.huabei_competition.event.EventReceiver;


import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import cn.jpush.im.android.api.JMessageClient;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;


/**
 * Create by FanChenYang at 2021/2/7
 */
public class MyApplication extends Application {
    public static final String URL = "http://192.168.115.60:8080";
    private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    public OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
                    cookieStore.put(httpUrl.host(), list);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        // 极光IM
        JMessageClient.init(this);
        // 通知栏
        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_DISABLE);
        JMessageClient.registerEventReceiver(EventReceiver.getInstance());
//        JMessageClient.setNotificationFlag(JMessageClient.FLAG_NOTIFY_WITH_SOUND);
        LitePal.initialize(this);
    }


    private static final String TAG = "MyApplication";

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventReceiver.unRegisterEventReceiver();
        Log.d(TAG, "onTerminate: ");
    }

    @NonNull
    public String loadOneQuote() {
        try (BufferedReader inputStream = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.quote)))) {
            Random random = new Random();
            int i1 = random.nextInt(50);
            for (int i = 0; i < 50; i++) {
                String s = inputStream.readLine();
                if (i == i1) {
                    return s;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 通过反射拿到当前app的application
     *
     * @return 返回application
     */
    public static MyApplication getApplicationByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object thread = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(thread);
            if (app == null) {
                throw new NullPointerException("u should init first");
            }
            return (MyApplication) app;
        } catch (NoSuchMethodException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new NullPointerException("u should init first");
    }

}
