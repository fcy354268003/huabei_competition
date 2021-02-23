package com.example.huabei_competition.db;

import androidx.recyclerview.widget.DiffUtil;

import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.widget.DiffDataCallback;

import org.litepal.crud.LitePalSupport;

import java.util.Objects;

public class GroupApply extends LitePalSupport implements DiffDataCallback.Differ<GroupApply> {
    private String groupId;
    private int state;
    private String acquirerUserName;
    private String userName;
    private String reason;
    private String groupName;

    public GroupApply(String groupId, String userName, String reason) {
        this.groupId = groupId;
        this.acquirerUserName = UserUtil.sUserName;
        this.reason = reason;
        this.state = 0;
        this.userName = userName;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getGroupId() {
        return groupId;
    }

    public int getState() {
        return state;
    }

    public String getAcquirerUserName() {
        return acquirerUserName;
    }

    public String getUserName() {
        return userName;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupApply apply = (GroupApply) o;
        return groupId.equals(apply.groupId) &&
                acquirerUserName.equals(apply.acquirerUserName) &&
                userName.equals(apply.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, acquirerUserName, userName);
    }

    @Override
    public boolean isSame(GroupApply apply) {
        return equals(apply);
    }

    @Override
    public boolean isContentSame(GroupApply apply) {
        return state == apply.getState() && reason.equals(apply.getReason());
    }
}
