package com.example.huabei_competition.network.api;

import com.example.huabei_competition.db.Dialogue;
import com.example.huabei_competition.db.FriendCircle;
import com.example.huabei_competition.db.NPC;
import com.example.huabei_competition.db.Prop;
import com.example.huabei_competition.db.ShopRole;
import com.example.huabei_competition.event.ChatRoomUtil;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Create by FanChenYang at 2021/2/27
 */
public class NPCRel {
    public static final Gson gson = new Gson();
    public static final String PATH_MONEY = "/queryMoney";
    public static final OkHttpClient client = new OkHttpClient();
    public static final String PATH_GET_PRO_LIST = "/story/getPropList";
    public static final String PATH_GET_ROLE = "/story/getRoleList";
    public static final String PATH_BUY_PROP = "/story/buyProps";
    public static final String PATH_GET_MINE_NPC = "/role/getInfo";
    public static final String PATH_GET_DIALOGUE = "/role/getDialogue";
    public static final String PATH_REPLY = "/role/reply";

    private static class MoneyGet {
        String token = LogIn.TOKEN;
    }

    public static class MoneyGet_1 {
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
            /**
             * money : money
             */
            private String money;

            public String getMoney() {
                return money;
            }
        }
    }

    /**
     * 查询铜钱
     */
    public static void queryMoney(Callback callback) {
        MoneyGet moneyGet = new MoneyGet();
        String json = gson.toJson(moneyGet);
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_MONEY)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    private static class A {

    }

    /**
     * 查询商店道具列表
     */
    public static void queryProduct(Callback callback) {
        String json = gson.toJson(new A());
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_PRO_LIST)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static class PropResponse {
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
            private String count;
            ArrayList<Prop> info = new ArrayList<>();

            public String getCount() {
                return count;
            }

            public ArrayList<Prop> getInfo() {
                return info;
            }
        }
    }

    /**
     * 查询商店角色列表
     */
    public static void getRoleList(Callback callback) {
        String json = gson.toJson(new MoneyGet());
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_ROLE)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static class ShopRoleResponse {
        private String code;
        private String message;
        private DataDTO data;

        public static class DataDTO {
            private String count;
            ArrayList<ShopRole> info = new ArrayList<>();

            public String getCount() {
                return count;
            }

            public ArrayList<ShopRole> getInfo() {
                return info;
            }
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public DataDTO getData() {
            return data;
        }
    }

    //===============================================================
    private static class Buy {
        String id;
        String token = LogIn.TOKEN;

        public Buy(String id) {
            this.id = id;
        }
    }

    public static class Buy_1 {
        private String code;
        private String message;
        private DataDTO data;

        public static class DataDTO {
            private String money;

            public String getMoney() {
                return money;
            }
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public DataDTO getData() {
            return data;
        }
    }

    /**
     * @param id 商品或者商店角色id
     */
    public static void buyProp(String id, Callback callback) {
        String json = gson.toJson(new Buy(id));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_BUY_PROP)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    //=====================================================================
    public static void getNPCList(Callback callback) {
        String json = gson.toJson(new MoneyGet());
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_MINE_NPC)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static class Get_NPC_1 {
        private String code;
        private String message;
        private DataDTO data;

        public static class DataDTO {
            private String count;
            List<NPC> info = new ArrayList<>();

            public String getCount() {
                return count;
            }

            public List<NPC> getInfo() {
                return info;
            }
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public DataDTO getData() {
            return data;
        }
    }

    //===================================================================================
// 获取角色对话
    public static void getDialogue(String id, Callback callback) {
        String json = gson.toJson(new Buy(id));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_GET_DIALOGUE)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }


    public static class DialogueResponse {
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
            public String getIsContinue() {
                return isContinue;
            }

            public Dialogue getInfo() {
                return info;
            }

            private String isContinue;
            private Dialogue info;
            private FriendCircle pyc;
        }
    }

    //=========================================================================================
//回复角色消息
    public static class Reply {
        private String token;
        private String id;
        private int order;
        private String reid;

        public Reply(String id, int order, String reid) {
            this.token = LogIn.TOKEN;
            this.id = id;
            this.order = order;
            this.reid = reid;
        }
    }

    /**
     *
     * @param id 角色id
     * @param order 第几条 从0开始
     * @param reid 对话id
     */
    public static void replyDialogue(String id, int order, String reid, Callback callback) {
        String json = gson.toJson(new Reply(id,order,reid));
        RequestBody requestBody = RequestBody.create(ChatRoomUtil.JSON, json);
        Request request = new Request.Builder()
                .url(LogIn.BASIC_PATH + PATH_REPLY)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
