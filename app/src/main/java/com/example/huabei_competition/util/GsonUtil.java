package com.example.huabei_competition.util;

import com.google.gson.Gson;

class GsonUtil {
    private static final Gson gson = new Gson();

    public static String toGson(Object o) {
        return gson.toJson(o);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
