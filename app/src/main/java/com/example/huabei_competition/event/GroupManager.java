package com.example.huabei_competition.event;

import java.io.File;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupBasicInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.api.BasicCallback;

public class GroupManager {
    /**
     * @param groupName 群昵称
     * @param groupDesc 群简介
     * @param callback  回调
     */
    public static void createGroup(String groupName, String groupDesc, File avatarFile, String format, CreateGroupCallback callback) {
        // TODO 向后端发送请求  是否改昵称已存在
        if (avatarFile != null)
            JMessageClient.createPublicGroup(groupName, groupDesc, avatarFile, format, callback);
        else JMessageClient.createPublicGroup(groupName, groupDesc, callback);
    }

    private static void isNameStandard() {

    }


    /**
     * @param groupID  要解散的群名称
     * @param callback 回调接口
     */
    public static void adminDissolveGroup(long groupID, BasicCallback callback) {
        JMessageClient.adminDissolveGroup(groupID, callback);
    }

    /**
     * 拿到用户加入的所有群的ID
     *
     * @param callback 回调
     */
    public static void getGroupIdList(GetGroupIDListCallback callback) {
        JMessageClient.getGroupIDList(callback);
    }

    // 拿到该应用下所有公开群的信息
    public static void getPublicGroupListByApp(String appKey, int start, int count, RequestCallback<List<GroupBasicInfo>> callback) {
        JMessageClient.getPublicGroupListByApp(appKey, start, count, callback);
    }

    public static void getGroupByID(long groupId, GetGroupInfoCallback callback) {
        JMessageClient.getGroupInfo(groupId, callback);
    }

    public static void getGroupMembers(long groupId, RequestCallback<List<GroupMemberInfo>> callback) {
        JMessageClient.getGroupMembers(groupId, callback);
    }

    /**
     * @param groupID      待加群的群组ID。创建群组时返回的。
     * @param appKey       被添加的群成员所属的appkey，不填则默认为本应用appkey
     * @param userNameList 群组成员列表，使用成员 username
     * @param callback     结果回调
     */
    public static void addGroupMembers(long groupID, String appKey, List<String> userNameList, BasicCallback callback) {
        JMessageClient.addGroupMembers(groupID, appKey, userNameList, callback);
    }

    /**
     * @param groupId      待删除成员的群ID。
     * @param appKey       被移除的群成员所属的appkey，不填则默认为本应用appkey
     * @param usernameList 待删除的成员列表。
     * @param callback     结果回调。
     */
    public static void removeGroupMembers(long groupId, String appKey, List<String> usernameList, BasicCallback callback) {
        JMessageClient.removeGroupMembers(groupId, appKey, usernameList, callback);
    }

    /**
     * @param groupId  待退出的群ID。
     * @param callback 结果回调
     */
    public static void exitGroup(long groupId, BasicCallback callback) {
        JMessageClient.exitGroup(groupId, callback);
    }

    /**
     * 申请加群
     * @param groupId 群聊id
     * @param reason 申请原因
     * @param callback 回调
     */
    public static void applyToGroup(long groupId, String reason, BasicCallback callback) {
        JMessageClient.applyJoinGroup(groupId, reason, callback);
    }

}
