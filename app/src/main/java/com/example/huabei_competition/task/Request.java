package com.example.huabei_competition.task;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Request {
    private String url;
    private RequestMethod method;
    private List<KeyValue> keyValueList;

    public Request(String url) {
        this(url, RequestMethod.GET);
    }

    public Request(String url, RequestMethod method) {
        this.url = url;
        this.method = method;
        this.keyValueList = new ArrayList<>();
    }

    public void addValue(@NotNull String key, Object value) {
        keyValueList.add(new KeyValue(key, value));
    }

    @Override
    public String toString() {
        return "Request{" +
                "url='" + url + '\'' +
                ", method=" + method +
                ", keyValueList=" + keyValueList +
                '}';
    }
}
