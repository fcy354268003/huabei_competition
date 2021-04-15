package com.example.huabei_competition.task;

public interface HttpCallback {
    void onSuccess(Response response);
    void onFailure(Exception e);
}
