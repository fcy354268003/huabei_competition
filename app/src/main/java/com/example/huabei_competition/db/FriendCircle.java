package com.example.huabei_competition.db;

import com.example.huabei_competition.event.UserUtil;

import org.litepal.crud.LitePalSupport;

public class FriendCircle extends LitePalSupport {
    private String id;
    private String name;
    private String headPicture;
    private String content;
    private String picture;

    private String time;
    private final String userName = UserUtil.sUserName;

    public String getUserName() {
        return userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
