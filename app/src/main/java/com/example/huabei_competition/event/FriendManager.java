package com.example.huabei_competition.event;

import cn.jpush.im.android.api.ContactManager;

import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/1/27
 * 用于好友管理
 */
public class FriendManager {


    /**
     * @param targetUsername 被邀请方用户名
     * @param appKey         被邀请方用户的appKey,如果为空则默认从本应用appKey下查找用户。
     * @param reason         申请理由
     * @param callback       结果回调
     *                       <p>
     *                       callback 回调函数里的 responseCode == 0 好友请求 发送 成功 否则 为失败
     *                       </p>
     */
    public static void sendInvitationRequest(final String targetUsername, final String appKey, final String reason, final BasicCallback callback) {
        ContactManager.sendInvitationRequest(targetUsername, appKey, reason, callback);
    }

    /**
     * @param targetUsername 邀请方的用户名
     * @param appKey         邀请方用户的appKey,如果为空则默认从本应用appKey下查找用户。
     * @param callback       结果回调
     *                       <p>
     *                       接受对方邀请
     *                       </p>
     */
    public static void acceptInvitation(final String targetUsername, String appKey, final BasicCallback callback) {
        ContactManager.acceptInvitation(targetUsername, appKey, callback);
    }

    /**
     * @param targetUsername 邀请方用户名
     * @param appKey         邀请方用户的appKey,如果为空则默认从本应用appKey下查找用户
     * @param reason         拒绝理由
     * @param callback       callback 结果回调
     */
    public static void declineInvitation(final String targetUsername, String appKey, String reason, final BasicCallback callback) {
        ContactManager.declineInvitation(targetUsername, appKey, reason, callback);
    }

    /**
     * 获取用户好友列表
     *
     * @param listCallback 回调 responseCode == 0  成功
     */
    public static void getFriends(final GetUserInfoListCallback listCallback) {
        ContactManager.getFriendList(listCallback);
    }

    /**
     * @param userInfo 被删除的用户
     * @param callback 回调
     */
    public static void removeFromFriendList(UserInfo userInfo, BasicCallback callback) {
        userInfo.removeFromFriendList(callback);
    }
}
