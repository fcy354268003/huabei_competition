package com.example.huabei_competition.db;

import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

/**
 *      Create by FanChenYang at 2020/12/15
 *
 * Gson:
 *         "data"{
 *                 "id":"1"
 *                 "name":"陈青云",
 *                 "password":"123456",
 *                 "status":"0",
 *                 "favor":"0",
 *                 "time":"0"
 *     }
 */


public class User {

    @SerializedName("name")
    private String userName;
    @SerializedName("password")
    private String passWord;
    // 学习时长 单位 分钟
    @SerializedName("time")
    private int studyTime;
    @SerializedName("favor")
    private int likingValue;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public int getStudyTime() {
        return studyTime;
    }

    public void setStudyTime(int studyTime) {
        this.studyTime = studyTime;
    }

    public int getLikingValue() {
        return likingValue;
    }

    public void setLikingValue(int likingValue) {
        this.likingValue = likingValue;
    }

    @NotNull
    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                ", studyTime='" + studyTime + '\'' +
                ", likingValue=" + likingValue +
                '}';
    }
}

