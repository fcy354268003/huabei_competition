package com.example.huabei_competition.db;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

public class Blink extends LitePalSupport {
    // 发表的时间
    @SerializedName("createTime")
    private String time;
    // 哪个人物
    @SerializedName("momentName")
    private String story;
    // 动态内容
    @SerializedName("momentContent")
    private String content;
    @SerializedName("momentPhoto")
    private String id_img;

    @SerializedName("momentId")
    @Column(unique = true)
    private int order;

    private String easyTime;

    public String getEasyTime() {
        return easyTime;
    }

    public void setEasyTime(String easyTime) {
        this.easyTime = easyTime;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId_img() {
        return id_img;
    }

    @Override
    public String toString() {
        return "Blink{" +
                "time='" + time + '\'' +
                ", story='" + story + '\'' +
                ", content='" + content + '\'' +
                ", id_img='" + id_img + '\'' +
                ", order=" + order +
                ", easyTime='" + easyTime + '\'' +
                '}';
    }

    public void setId_img(String id_img) {

        this.id_img = id_img;
    }
}
