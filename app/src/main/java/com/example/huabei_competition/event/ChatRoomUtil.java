package com.example.huabei_competition.event;

import android.util.Base64;
import android.util.Log;


import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ChatRoomUtil {
    public static final String MASTER_SECRET = "e4e96f6fe56767f747a4db0a";
    public static final String APP_KEY = "95a34af8af751fe3af74ec74";
    public static final String KEY = "Authorization";
    public static String VALUE;
    public static final String BASIC_URL = "https://api.im.jpush.cn";
    public static final String CREATE_CHART_ROOM = "/v1/chatroom/";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    private static final String TAG = "ChatRoomUtil";
    static {
        String value = APP_KEY + ":" + MASTER_SECRET;
        byte[] bytes = value.getBytes();
        VALUE = " Basic " + Base64.encodeToString(bytes, 0, bytes.length, Base64.DEFAULT)
                .replaceAll("[\r\n]", "");
    }

    public static void createChatRoom(Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Gson gson = new Gson();
        String s = gson.toJson(new da());
        RequestBody requestBody = RequestBody.create(JSON, s);
        Request request = new Request.Builder()
                .url(BASIC_URL + CREATE_CHART_ROOM)
                .addHeader(KEY, VALUE)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    static class da {
        String name = UserUtil.sUserName + "asdas";
        String owner_username = UserUtil.sUserName;
    }

    /**
     * 返回请求示例
     * HTTP/1.1 200 OK
     * Content-Type: application/json
     * <p>
     * {
     * "total": 2,
     * "users": [
     * {
     * "username": "13538013231",
     * "flag": 0,
     * "room_ctime": "2017-11-17 08:57:54",
     * "mtime": "2017-10-30 17:24:17",
     * "ctime": "2017-10-30 17:24:17"
     * },
     * {
     * "username": "xia_12",
     * "flag": 0,
     * "room_ctime": "2017-11-16 19:13:07",
     * "mtime": "2017-02-08 17:56:04",
     * "ctime": "2017-02-08 17:56:04"
     * }
     * ],
     * "count": 2,
     * "start": 0
     * }
     */

    public static void getChatRoomUserList(Long roomId, CallBack callBack) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASIC_URL + "/v1/chatroom/" + roomId + "/members?start=0&count=6")
                .addHeader(KEY, VALUE)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + "获取用户列表");
                if (response.isSuccessful()) {
                    try {
                        List<String> userInfos = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int total = jsonObject.getInt("total");
                        Log.d(TAG, "onResponse: " + total);
                        JSONArray users = jsonObject.getJSONArray("users");
                        for (int j = 0; j < total; j++) {
                            JSONObject userJSONObject = users.getJSONObject(j);
                            String userId = userJSONObject.getString("username");
                            userInfos.add(userId);
                        }
                        callBack.onListCallBack(userInfos);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Log.d(TAG, "onResponse: " + response.body().string());
                response.close();
            }
        });
    }

    public static final String UPDATE_ROOM_INFO = "/v1/chatroom/{room_id}";

    public static void updateRoomState(String roomId, String ownerUserName, String des, Callback call) {
        ChatRoom chatRoom = new ChatRoom(ownerUserName, des);
        String json = new Gson().toJson(chatRoom);
        RequestBody requestBody = RequestBody.create(JSON, json);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BASIC_URL + UPDATE_ROOM_INFO.replace("{room_id}", roomId))
                .addHeader(KEY, VALUE)
                .put(requestBody)
                .build();
        client.newCall(request).enqueue(call);
    }

    static class ChatRoom {
        String ownerName;
        String roomName;
        String desc;

        public ChatRoom(String ownerName, String desc) {
            this.ownerName = ownerName;
            this.roomName = "roomName";
            this.desc = desc;
        }
    }

    public interface CallBack {
        void onListCallBack(List<String> userInfos);
    }
}
