package com.example.huabei_competition.util;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;


import com.example.huabei_competition.network.Network;
import com.example.huabei_competition.ui.activity.CheckInActivity;
import com.example.huabei_competition.widget.MyToast;


import java.util.List;

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

    /**
     * @param strings 表单输入情况
     *                <p>
     *                依次为：
     *                username
     *                password
     *                phoneNumber
     *                verification
     *                </>
     * @return true:填写符合规范
     */
    public static boolean register(List<String> strings, Callback callback) {
        Log.d(TAG, "register: " + strings);
        for (String string : strings) {
            if (TextUtils.isEmpty(string)) {
                MyToast.showMessage("请填写完整！");
                return false;
            }
        }
        if (!strings.get(1).equals(strings.get(2)))
            return false;
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("username", strings.get(0))
                .add("password", strings.get(1))
                .add("phoneNumber", strings.get(2))
                .add("verification", strings.get(3))
                .build();
        Request request = new Request.Builder().url(URL).post(body).build();
        client.newCall(request).enqueue(callback);
        return true;
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
