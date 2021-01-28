package com.example.huabei_competition.ui.fragments;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.network.Network;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.MessageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Create by FanChenYang at 2021/1/26
 * 单人聊天 或者 与 群聊天
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        initArgs();
    }

    // 当前是 单聊还是群聊
    private int TYPE = 0;
    public static final int PERSON = 0x1;
    public static final int GROUP = 0x2;
    // 单聊对象的用户名
    private String orientedUserName;
    // 群聊ID
    private long groupId;
    // 会话对象
    private Conversation conversation;
    private List<String> history;

    /**
     * 初始化 界面
     * 1   确定界面类型
     * 2   初始化参数信息
     */
    private void initArgs() {
        Bundle arguments = getArguments();
        // 默认是单聊
        TYPE = arguments.getInt("TYPE", 0x1);

        switch (TYPE) {
            case PERSON:
                //TODO 初始化 orientedUserName


                conversation = Conversation.createSingleConversation(orientedUserName);
                JMessageClient.enterSingleConversation(orientedUserName, null);
                break;
            case GROUP:
                //TODO 初始化 groupID


                conversation = Conversation.createGroupConversation(groupId);
                JMessageClient.enterGroupConversation(groupId);
                break;
        }
        List<Message> allMessage = conversation.getAllMessage();
        for (Message message : allMessage) {
            UserInfo fromUser = message.getFromUser();
            MessageContent content = message.getContent();
            String avatar = fromUser.getAvatar();
            String text = content.toJson();
            long userID = fromUser.getUserID();
            String userName = fromUser.getUserName();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JMessageClient.exitConversation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_talk, container, false);
    }

    /**
     * @return 是否成功发送消息
     */
    private boolean sendMessage(String text) {
        if (!Network.checkNetWorkState(getContext()))
            return false;
        Message message = conversation.createSendMessage(new TextContent(text));
        switch (TYPE) {
            case PERSON:
                break;
            case GROUP:
                break;
        }
        JMessageClient.sendMessage(message);
        return true;
    }

}