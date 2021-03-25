package com.example.huabei_competition.util;


import android.util.Log;

import com.example.huabei_competition.db.Dialogue;
import com.example.huabei_competition.db.FriendApply;
import com.example.huabei_competition.db.FriendCircle;
import com.example.huabei_competition.db.GroupApply;
import com.example.huabei_competition.db.Label;
import com.example.huabei_competition.db.NPC;
import com.example.huabei_competition.db.Prop;
import com.example.huabei_competition.db.ShopRole;
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

    private static final String TAG = "DatabaseUtil";

    public static int getNewFriendNotHandle() {
        Log.d(TAG, "getNewFriendNotHandle: " + UserUtil.sUserName);
        int friendSize = LitePal.where("acquirerUserName = ? and state = ?", UserUtil.sUserName, "0").find(FriendApply.class).size();
        int groupSize = LitePal.where("acquirerUserName = ? and state = ?", UserUtil.sUserName, "0").find(GroupApply.class).size();
        return friendSize + groupSize;
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

    public static void saveOrUpdateLabel(Label label) {
        label.saveOrUpdate(" userName = ? and type = ? and groupId = ?", UserUtil.sUserName, "1", String.valueOf(label.getGroupId()));
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

    public static void saveOrUpdateShopItem(Prop prop) {
        prop.saveOrUpdate("PropId = ?", prop.getPropId());
    }

    public static void saveOrUpdateShopRoleItem(ShopRole prop) {
        prop.saveOrUpdate("ShopRoleId = ?", prop.getShopRoleId());
    }

    public static void saveOrUpdateShopItem(NPC npc) {
        npc.saveOrUpdate("NPCID = ?", npc.getNPCID());
    }

    public static List<Prop> getProps() {
        List<Prop> all = LitePal.findAll(Prop.class);
        return all;
    }
    

    public static void saveOrUpdateNPC(NPC npc) {
        npc.saveOrUpdate("NPCID = ?", npc.getNPCID());
    }

    public static void saveOrUpdateDialogue(Dialogue npc) {
        npc.saveOrUpdate("NPCId = ? and userName = ? and Reid = ?", npc.getNPCId(), UserUtil.sUserName, npc.getReid());
    }

    public static List<NPC> getMyNPCs() {
        return LitePal.where("userName = ?", UserUtil.sUserName).find(NPC.class);
    }

    public static List<Dialogue> getStory(String id) {
        return LitePal.where("userName = ? and NPCId = ?", UserUtil.sUserName, id).find(Dialogue.class);
    }

    public static List<Dialogue> findByReid(String reid) {
        return LitePal.where("userName = ? and Reid = ?", UserUtil.sUserName, reid).find(Dialogue.class);
    }

    public static void saveOrUpdateFriendCircle(FriendCircle circle) {
        circle.saveOrUpdate("FCID = ?", circle.getFCID());
    }

    public static List<FriendCircle> getAllBlink() {
        return LitePal.where("userName = ?", UserUtil.sUserName).find(FriendCircle.class);
    }

    /**
     * 返回本地缓存的shopRole列表
     */
    public static List<ShopRole> getMineShopRole() {

        return LitePal.where("userName = ?",UserUtil.sUserName).find(ShopRole.class);
    }
}
