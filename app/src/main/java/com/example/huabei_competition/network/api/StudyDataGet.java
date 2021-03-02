package com.example.huabei_competition.network.api;

import com.example.huabei_competition.event.ChatRoomUtil;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class StudyDataGet {
    public static final String PATH_POST_TEAM = "/postTeamTime";
    public static final String PATH_POST_PERSON = "/postPersonTime";
    public static final String PATH_GET_USER_INFO = "/getUserInfo";
    public static final OkHttpClient client = new OkHttpClient();
    public static Gson gson = new Gson();

    public static class Data {
        String token = LogIn.TOKEN;
        String username;

        public Data(String username) {
            this.username = username;
        }
    }

    public static class UserData {

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
            public String getAllTime() {
                return allTime;
            }

            public String getAllNumber() {
                return allNumber;
            }

            public String getAvgTime() {
                return avgTime;
            }

            public String getTime() {
                return time;
            }

            public String getNumber() {
                return number;
            }

            public List<InfoDTO> getInfo() {
                return info;
            }

            private String allTime;
            private String allNumber;
            private String avgTime;
            private String time;
            private String number;
            private List<InfoDTO> info;

            public static class InfoDTO {
                private String name;
                private String size;

                public String getName() {
                    return name;
                }

                public String getSize() {
                    return size;
                }
            }
        }
    }

    public static void getUserData(String userName, Callback callback) {
        String json = gson.toJson(new Data(userName));
        RequestBody body = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_USER_INFO)
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void submitTimePerson(SubmitTime submitTime, Callback callback) {
        String s = gson.toJson(submitTime);
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, s);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_POST_PERSON)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void submitTimeTeam(SubmitTime submitTime, Callback callback) {
        String s = gson.toJson(submitTime);
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, s);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_POST_TEAM)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static class SubmitTime {

        private String token = LogIn.TOKEN;
        private String time;
        private String sign;
        private String label;

        public SubmitTime(String time, String sign, String label) {
            this.time = time;
            this.sign = sign;
            this.label = label;
        }
    }
}
