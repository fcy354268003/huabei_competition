package com.example.huabei_competition.db;


import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.widget.DiffDataCallback;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.Objects;


/**
 * Create by FanChenYang at 2021/1/29
 * <p>
 * 用来处理好友申请
 * </p>
 */
public class FriendApply extends LitePalSupport implements DiffDataCallback.Differ<FriendApply> {
    // 申请接收方用户用户名
    private String acquirerUserName;
    private String appKey;
    private String userName;
    private String reason;
    // 被处理状态 0 未处理 1 接受 2 未接受
    private int state;

    public FriendApply(String appKey, String userName, String reason) {
        this.appKey = appKey;
        this.userName = userName;
        this.reason = reason;
        state = 0;
        acquirerUserName = UserUtil.sUserName;
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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendApply that = (FriendApply) o;
        return acquirerUserName.equals(that.acquirerUserName) &&
                userName.equals(that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acquirerUserName, userName);
    }

    @Override
    public boolean isSame(FriendApply apply) {
        return equals(apply);
    }

    @Override
    public boolean isContentSame(FriendApply apply) {
        return reason.equals(apply.getReason()) && state == apply.getState();
    }
}
