package com.example.huabei_competition.db;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Dialogue extends LitePalSupport {

    private String Reid;
    private List<String> content;
    private List<String> reply;
    // 哪个npc
    private String id;

    public String getReid() {
        return Reid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReid(String reid) {
        Reid = reid;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public List<String> getReply() {
        return reply;
    }

    public void setReply(List<String> reply) {
        this.reply = reply;
    }
}
