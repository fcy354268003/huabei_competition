package com.example.huabei_competition.db;

public class FriendApply {
    private String appKey;
    private String userName;
    private String reason;

    public FriendApply(String appKey, String userName, String reason) {
        this.appKey = appKey;
        this.userName = userName;
        this.reason = reason;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getReason() {
        return reason;
    }
}
