package com.example.huabei_competition.db;

import com.example.huabei_competition.event.UserUtil;

import org.litepal.crud.LitePalSupport;

import java.util.Objects;

/**
 * Create by FanChenYang at 2021/2/13
 */
public class Label extends LitePalSupport {
    private String labelName;
    private String userName;
    // 区分是自习室还是学习内容标签 单人标签：0 自习室：1
    private int type;
    private long groupId;

    public Label(String labelName) {
        this.labelName = labelName;
        this.userName = UserUtil.sUserName;
        type = 0;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Label label = (Label) o;
        return type == label.type &&
                groupId == label.groupId &&
                labelName.equals(label.labelName) &&
                userName.equals(label.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelName, userName, type, groupId);
    }
}
