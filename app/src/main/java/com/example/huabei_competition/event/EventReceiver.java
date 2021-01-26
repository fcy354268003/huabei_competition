package com.example.huabei_competition.event;


import android.util.Log;

import java.util.HashMap;
import java.util.List;

import cn.jmessage.biz.httptask.task.GetEventNotificationTaskMng;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.DeviceInfo;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/1/26
 * 接受消息事件，并分发
 * 事件接受默认是在子线程
 * 用户在线期间收到的消息都会以MessageEvent的方式上抛
 */
public class EventReceiver {
    private volatile static EventReceiver eventReceiver;

    private EventReceiver() {
    }


    public static EventReceiver getInstance() {
        if (eventReceiver == null) {
            synchronized (EventReceiver.class) {
                if (eventReceiver == null)
                    eventReceiver = new EventReceiver();
            }
        }
        return eventReceiver;
    }

    public static void unRegisterEventReceiver() {
        JMessageClient.unRegisterEventReceiver(eventReceiver);
    }

    /**
     * 接收消息
     *
     * @param eventEntity 事件实体
     */
    public void onEvent(GetEventNotificationTaskMng.EventEntity eventEntity) {
        // TODO  1 如果正在聊天，刷新正在聊天界面 2 刷新联系人界面
    }

    /**
     * 主线程处理
     */
    public void onEventMainThread(GetEventNotificationTaskMng.EventEntity eventEntity) {


    }

    //用户在线期间，如果群组中发生了成员变化事件，sdk也会通过上抛MessageEvent的方式来通知上层
    public void onEvent(MessageEvent event) {
        Log.d(TAG, "onEvent: 收到消息");
        Message msg = event.getMessage();
        //获取消息类型，如text voice image eventNotification等。
        switch (msg.getContentType()) {
            //处理事件提醒消息，此处message的contentType类型为eventNotification。
            case eventNotification:
                //获取事件发生的群的群信息
                GroupInfo groupInfo = (GroupInfo) msg.getTargetInfo();
                //获取事件具体的内容对象
                EventNotificationContent eventNotificationContent = (EventNotificationContent) msg.getContent();
                //获取事件具体类型
                switch (eventNotificationContent.getEventNotificationType()) {
                    case group_member_added:
                        //群成员加群事件
                        Log.d(TAG, "onEvent: 群成员加群事件");
                        break;
                    case group_member_removed:
                        //群成员被踢事件
                        Log.d(TAG, "onEvent: 群成员被踢事件");
                        break;
                    case group_member_exit:
                        //群成员退群事件
                        Log.d(TAG, "onEvent: 群成员退群事件");
                        break;
                    case group_info_updated://since 2.2.1
                        //群信息变更事件
                        Log.d(TAG, "onEvent: 群信息变更事件");
                        break;
                }
                break;
            case text:
                Log.d(TAG, "onEvent: 文字消息");
                // 消息发送者信息
                UserInfo sender = msg.getFromUser();
                TextContent content = (TextContent) msg.getContent();
                Log.d(TAG, "onEvent: 收到消息" + content.getText());
                break;
        }
    }

    private static final String TAG = "EventReceiver";
    private int x = 1;

    public void test() {

        if (x++ == 1) {
            JMessageClient.login("java", "a1575047842A", new RequestCallback<List<DeviceInfo>>() {
                @Override
                public void gotResult(int i, String s, List<DeviceInfo> deviceInfos) {
                    for (DeviceInfo deviceInfo : deviceInfos) {
                        Log.d(TAG, "gotResult: " + deviceInfo.getDeviceID());
                    }
                }
            });
            return;
        }


        Conversation conversation = Conversation.createSingleConversation("java");
        Message message = conversation.createSendMessage(new TextContent("你好"));
        JMessageClient.sendMessage(message);
        Log.d(TAG, "test: 发送消息");

    }

}
