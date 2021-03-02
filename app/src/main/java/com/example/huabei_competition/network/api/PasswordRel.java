package com.example.huabei_competition.network.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.huabei_competition.event.ChatRoomUtil;
import com.google.gson.Gson;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Create by FanChenYang at 2021/2/27
 */
public class PasswordRel {
    private static final Gson gson = new Gson();
    private static final OkHttpClient client = new OkHttpClient();
    public static final String PATH_CHANGE_PASS = "/changePassword";

    /**
     * @param callback 先调用自己后端的接口 成功以后调用极光接口
     */
    public static void changePass(@NonNull String oldPass, @NonNull String newPass, @NonNull Callback callback) {
        Change change = new Change(oldPass, newPass);
        String json = gson.toJson(change);
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_CHANGE_PASS)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private static class Change {
        String token;
        String oldPassword;
        String newPassword;

        public Change(String oldPassword, String newPassword) {
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
            token = LogIn.TOKEN;
        }
    }

    public static final String FOR_1 = "/forget/getUser";
    public static final String FOR_2 = "/forget/verification";
    public static final String FOR_3 = "/forget/checkVerification";
    public static final String FOR_4 = "/forget/changePassword";

    /**
     * @param username 用户名 可以是 手机号
     */
    public static void forget_1(String username, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        String json = gson.toJson(new Forget_1(username));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + FOR_1)
                .post(requestBody)
                .build();
        Log.d(TAG, "forget_1: ");
        client.newCall(request).enqueue(callback);
    }

    private static final String TAG = "PasswordRel";

    /**
     * 第一次返回信息
     */
    public static class Forget_1_1 {

        /**
         * code : code
         * message : message
         * data : {"phone":"phone","token":"token"}
         */

        private String code;
        private String message;
        private DataDTO data;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public DataDTO getData() {
            return data;
        }

        public static class DataDTO {
            public String getPhone() {
                return phone;
            }

            public String getToken() {
                return token;
            }

            /**
             * phone : phone
             * token : token
             */
            private String phone;
            private String token;
        }
    }

    private static class Forget_1 {
        String username;

        public Forget_1(String username) {
            this.username = username;
        }
    }
//=========================================================================================

    /**
     * 发送短信
     */
    private static class Forget_2 {
        // 第一次返回的token
        String token;

        public Forget_2(String token) {
            this.token = token;
        }
    }

    /**
     * @param token 第一次返回的token
     */
    public static void forget_2(String token, Callback callback) {
        String json = gson.toJson(new Forget_2(token));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + FOR_2)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
//==================================================================================================

    /**
     * @param token        d第一次返回的token
     * @param verification 用户输入的验证码
     */
    public static void forget_3(String token, String verification, Callback callback) {
        String json = gson.toJson(new Forget_3(token, verification));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + FOR_3)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private static class Forget_3 {
        // 第一次返回的token
        String token;
        String verification;

        public Forget_3(String token, String verification) {
            this.token = token;
            this.verification = verification;
        }
    }

    public static class Forget_3_3 {

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public DataDTO getData() {
            return data;
        }

        /**
         * code : code
         * message : message
         * data : {"token":"token"}
         */

        private String code;
        private String message;
        private DataDTO data;

        public static class DataDTO {
            public String getToken() {
                return token;
            }

            /**
             * token : 用户更改密码权限的token
             */
            private String token;
        }
    }

    //========================================================================================================================
    private static class Forget_4 {
        String token;
        String password;

        public Forget_4(String token, String password) {
            this.token = token;
            this.password = password;
        }
    }

    public static void forget_4(String token, String pass, Callback callback) {
        String json = gson.toJson(new Forget_4(token, pass));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + FOR_4)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static class Forget_4_4 {
        String code;
        String message;

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}
