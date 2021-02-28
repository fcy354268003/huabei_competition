package com.example.huabei_competition.db;

import com.example.huabei_competition.event.UserUtil;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by FanChenYang at 2021/2/28
 */
public class Dialogue extends LitePalSupport {
    // 用户选了哪一句回答 1或者2
    private int WhichOne = -1;
    private String Reid;
    private List<String> content = new ArrayList<>();
    private List<String> reply = new ArrayList<>();
    // 哪个npc
    private String id;
    private String userName = UserUtil.sUserName;

    public void setWhichOne(int whichOne) {
        WhichOne = whichOne;
    }

    public int getWhichOne() {
        return WhichOne;
    }

    public String getUserName() {
        return userName;
    }

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
