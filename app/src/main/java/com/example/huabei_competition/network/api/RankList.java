package com.example.huabei_competition.network.api;

import com.example.huabei_competition.event.ChatRoomUtil;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RankList {
    private static final Gson gson = new Gson();
    public static final String PATH_GET_EMERGING = "/rankingList/getEmerging";
    public static final String PATH_GET_GENERAL_LIST = "/rankingList/getGeneral";
    private static final OkHttpClient client = new OkHttpClient();

    private static class Rank {
        String token = LogIn.TOKEN;
    }

    private static String getJson() {
        return gson.toJson(new Rank());
    }

    public static void getEmerging(Callback callback) {
        String json = getJson();
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_EMERGING)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void getGeneralList(Callback callback) {
        String json = getJson();
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_GENERAL_LIST)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 新晋榜返回
     */
    public static class Emerging {
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
            private String total;
            private List<InfoDTO> info;

            public static class InfoDTO {
                public String getId() {
                    return id;
                }

                public String getTime() {
                    return time;
                }

                private String id;
                private String time;
            }

            public String getTotal() {
                return total;
            }

            public List<InfoDTO> getInfo() {
                return info;
            }
        }
    }

    /**
     * 总榜返回
     */
    public static class General {
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
            public String getTotal() {
                return total;
            }

            public List<InfoDTO> getInfo() {
                return info;
            }

            private String total;
            private List<InfoDTO> info;

            public static class InfoDTO {
                @SerializedName("id")
                private String groupId;
                private String allTime;

                public String getGroupId() {
                    return groupId;
                }

                public String getAllTime() {
                    return allTime;
                }
            }
        }
    }
}
