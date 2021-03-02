package com.example.huabei_competition.db;

import com.example.huabei_competition.event.UserUtil;
import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

public class NPC extends LitePalSupport {
    @SerializedName("id")
    private String NPCID;
    private String name;
    // 朝代
    private String dynasty;
    //字号
    private String tradename;
    @SerializedName("picture")
    private String headPicture;
    private String portrait;
    private String description;
    private String favor;
    private String isDialogue;
    private String userName = UserUtil.sUserName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNPCID() {
        return NPCID;
    }

    public void setNPCID(String NPCID) {
        this.NPCID = NPCID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDynasty() {
        return dynasty;
    }

    public void setDynasty(String dynasty) {
        this.dynasty = dynasty;
    }

    public String getTradename() {
        return tradename;
    }

    public void setTradename(String tradename) {
        this.tradename = tradename;
    }

    public String getHeadPicture() {
        return headPicture;
    }

    public void setHeadPicture(String headPicture) {
        this.headPicture = headPicture;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFavor() {
        return favor;
    }

    public void setFavor(String favor) {
        this.favor = favor;
    }

    public String getIsDialogue() {
        return isDialogue;
    }

    public void setIsDialogue(String isDialogue) {
        this.isDialogue = isDialogue;
    }
}
