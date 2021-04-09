package com.example.huabei_competition.event;

import android.content.Intent;
import android.text.TextUtils;


import com.example.huabei_competition.network.Network;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.ui.activity.CheckInActivity;
import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.MyToast;


import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
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
    public static String sUserName;
    public static void logIn(BaseActivity activity, String userName, String password,LogIn.LogCallback callback) {
        if (!Network.checkNetWorkState(activity)) {
            MyToast.showMessage("网络不可用");
            return;
        }
        if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
            MyToast.showMessage("请将用户名和密码填写完整");
            return;
        }
        activity.startLoading();
        LogIn.login(userName,password,callback);
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
     * @return true:成功发送请求
     */
//    public static boolean register(List<String> strings, Callback callback, BasicCallback imCallBack) {
//        if (isStandard(strings)) {
//            registerIM(strings, imCallBack);
////            registerOwnBack(strings, callback);
//            return true;
//        }
//        return false;
//    }
//
    /**
     * @param strings  注册页面填写内容
     * @param callback 回到接口
     */
    private static void registerOwnBack(List<String> strings, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("username", strings.get(0))
                .add("password", strings.get(1))
                .add("phoneNumber", strings.get(2))
                .add("verification", strings.get(3))
                .build();
        Request request = new Request.Builder().url(URL).post(body).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * @param strings  注册页面填写内容
     * @param callback 回调接口
     */
    private static void registerIM(List<String> strings, BasicCallback callback) {
        JMessageClient.register(strings.get(0), strings.get(1), callback);
    }

    public static void logOut(BaseActivity baseActivity) {
        JMessageClient.logout();
        //初始化Application中user的信息
        MyApplication myApplication = MyApplication.getApplicationByReflect();
        baseActivity.logOut();
        // 跳转到登陆界面
        baseActivity.startActivity(new Intent(baseActivity, CheckInActivity.class));
    }

    private static final String TAG = "UserUtil";

}
