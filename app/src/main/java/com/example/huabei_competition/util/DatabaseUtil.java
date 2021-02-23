package com.example.huabei_competition.util;


import com.example.huabei_competition.db.FriendApply;
import com.example.huabei_competition.db.GroupApply;
import com.example.huabei_competition.db.Label;
import com.example.huabei_competition.event.UserUtil;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.List;

public class DatabaseUtil {
    /**
     * @return 拿到当前用户的所有申请记录
     */
    public static List<FriendApply> getNewFriends() {
        List<FriendApply> all = LitePal.where("acquirerUserName = ?", UserUtil.sUserName).find(FriendApply.class);
        return all;
    }

    public static int getNewFriendNotHandle() {
        return LitePal.where("acquirerUserName = ? and state = ?", UserUtil.sUserName, "0").find(FriendApply.class).size();
    }

    /**
     * @return 返回申请入群相关
     */
    public static List<GroupApply> getNewGroupMembers() {
        List<GroupApply> applies = LitePal.where("acquirerUserName = ?", UserUtil.sUserName).find(GroupApply.class);
        return applies;
    }

    /**
     * @param o 要保存的对象
     * @return 是否成功保存
     */
    public static boolean save(Object o) {
        if (o instanceof LitePalSupport) {
            ((LitePalSupport) o).save();
            return true;
        }
        return false;
    }
    public static void saveOrUpdateLabel(Label label){
        label.saveOrUpdate(" userName = ? and type = ? and groupId = ?",UserUtil.sUserName,"1", String.valueOf(label.getGroupId()));
    }
    /**
     * @param apply 更新数据库新添加朋友情况
     */
    public static void updateNewFriendApply(FriendApply apply) {
        apply.saveOrUpdate(
                "acquirerUserName = ? and userName = ?",
                UserUtil.sUserName, apply.getUserName());
    }

    /**
     * @param apply 更新数据库新添加申请入群情况
     */
    public static void updateNewGroupMemberApply(GroupApply apply) {
        apply.saveOrUpdate(
                "acquirerUserName = ? and userName = ?",
                UserUtil.sUserName, apply.getUserName());
    }

    public static List<Label> getLabels() {
        List<Label> labels = LitePal.where(" userName = ?", UserUtil.sUserName).find(Label.class);
        return labels;
    }

    public static List<Label> getPersonLabels() {
        List<Label> labels = LitePal.where(" userName = ? and type = ?", UserUtil.sUserName, "0").find(Label.class);
        return labels;
    }

    public static List<Label> getGroupLabels() {
        List<Label> labels = LitePal.where(" userName = ? and type = ?", UserUtil.sUserName, "1").find(Label.class);
        return labels;
    }
}
