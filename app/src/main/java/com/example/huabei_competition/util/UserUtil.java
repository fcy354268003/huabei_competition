package com.example.huabei_competition.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;


import com.example.huabei_competition.network.Network;
import com.example.huabei_competition.ui.activity.CheckInActivity;
import com.example.huabei_competition.widget.MyToast;


import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;


/**
 * Create by FanChenYang at 2020/12/19
 * Intent:
 * 用来管理对用户的一些操作，用户仍然在Application中管理
 * 1.登陆
 * 2.注册
 * 3.注销
 */

public class UserUtil {
    private static final String URL = MyApplication.URL + "/app/login";

    public static void logIn(BaseActivity activity, String userName, String password, Callback callback) {
        if (!Network.checkNetWorkState(activity)) {
            MyToast.showMessage("网络不可用");
            return;
        }

        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(userName)) {
            activity.startLoading();
            MyApplication application = (MyApplication) activity.getApplication();
            OkHttpClient client = application.okHttpClient;
            FormBody formBody = new FormBody.Builder()
                    .add("username", userName)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder().post(formBody).url(URL).build();
            client.newCall(request).enqueue(callback);
        } else MyToast.showMessage("登陆失败");
    }

    public static void register(Activity activity, String userName, String password, Callback callback) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
            MyToast.showMessage("请填写符合规范的账户名与密码");
            return;
        }
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("username", userName)
                .add("password", password)
                .build();
        Request request = new Request.Builder().url(URL).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    public static void logOut(BaseActivity baseActivity) {
        //初始化Application中user的信息
        MyApplication myApplication = MyApplication.getApplicationByReflect();
        myApplication.setUser(null);
        baseActivity.logOut();
        // 跳转到登陆界面
        baseActivity.startActivity(new Intent(baseActivity, CheckInActivity.class));
    }

    private static final String TAG = "UserUtil";

}
