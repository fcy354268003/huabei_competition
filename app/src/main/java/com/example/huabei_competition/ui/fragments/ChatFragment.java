package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.ChatCallback;
import com.example.huabei_competition.databinding.FragmentChatBinding;
import com.example.huabei_competition.event.EventReceiver;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.network.Network;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Create by FanChenYang at 2021/1/26
 * 单人聊天 或者 与 群聊天
 */
public class ChatFragment extends Fragment implements ChatCallback {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initArgs();
    }

    // PERSON : TYPE :int, UserName :String
    // GROUP : TYPE :int ,UserName :String
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_USERNAME = "UserName";
    public static final String KEY_GROUPId = "GroupId";
    // 当前是 单聊还是群聊
    private int TYPE = 0;
    public static final int PERSON = 0x1;
    public static final int GROUP = 0x2;
    // 单聊对象的用户名
    private String orientedUserName;
    // 群聊ID
    private long groupId;
    private GroupInfo mGroupInfo;
    // 会话对象
    private Conversation conversation;
    private UserInfo orientedUserInfo;
    private boolean mViscosity;

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
                orientedUserName = arguments.getString(KEY_USERNAME);
                conversation = Conversation.createSingleConversation(orientedUserName);
                orientedUserInfo = (UserInfo) conversation.getTargetInfo();
                JMessageClient.enterSingleConversation(orientedUserName, null);
                break;
            case GROUP:
                //TODO 初始化 groupID
                groupId = Long.parseLong(arguments.getString(KEY_GROUPId));
                conversation = Conversation.createGroupConversation(groupId);
                mGroupInfo = (GroupInfo) conversation.getTargetInfo();
                JMessageClient.enterGroupConversation(groupId);
                break;
        }
        mViscosity = EventReceiver.getInstance().getIsViscosity(getClass().getSimpleName());
        // TODO 初始化 聊天记录
        conversation.resetUnreadCount();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        JMessageClient.exitConversation();
    }

    private FragmentChatBinding binding;
    private MyRecyclerAdapter<Message> messageMyRecyclerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chat, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallBack(this);
        binding.toolbar.setNavigationOnClickListener(back -> {
            onBackClick();
        });
        if (groupId == 0)
            initUser();
        else initGroup();
        initAdapter();
        observeMessage();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (groupId != 0) {
            binding.ivMore.setVisibility(View.VISIBLE);
            binding.ivMore.setOnClickListener(this::toGroupInfo);
        }
    }

    private void toGroupInfo(View view) {
        JMessageClient.getGroupInfo(groupId, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int i, String s, GroupInfo groupInfo) {
                if (i == 0) {
                    LiveDataManager.getInstance().<GroupInfo>with(GroupInfoFragment.class.getSimpleName()).setValue(groupInfo);
                    ((MainActivity) getActivity()).getController().navigate(R.id.action_chatFragment_to_groupInfoFragment);
                }
            }
        });
    }

    private void initUser() {
        String nickname = orientedUserInfo.getNickname();
        String userName = orientedUserInfo.getUserName();
        binding.tvTitle.setText(nickname.equals("") ? userName : nickname);
    }

    private void initGroup() {
        binding.tvTitle.setText(mGroupInfo.getGroupName());
    }

    private void observeMessage() {
        MutableLiveData<Message> messageMutableLiveData = LiveDataManager.getInstance().with(ChatFragment.class.getSimpleName());
        messageMutableLiveData.observe(getViewLifecycleOwner(), new Observer<Message>() {
            private boolean isFirst = mViscosity;

            @Override
            public void onChanged(Message message) {
                conversation.resetUnreadCount();

                if (isFirst) {
                    isFirst = false;
                    return;
                }
                if (orientedUserName != null) {
                    // 当前是单聊
                    if (message.getFromUser().getUserName().equals(orientedUserName))
                        addMessage(message);
                } else {
                    // 当前是群聊
                    GroupInfo groupInfo = (GroupInfo) message.getTargetInfo();
                    if (groupInfo.getGroupName().equals(mGroupInfo.getGroupName())) {
                        addMessage(message);
                    }
                }
            }
        });
    }

    private static final String TAG = "ChatFragment";

    /**
     * 初始化聊天记录
     */
    private void initAdapter() {
        if (messageMyRecyclerAdapter == null) {
            messageMyRecyclerAdapter = new MyRecyclerAdapter<Message>(conversation.getAllMessage()) {
                @Override
                public int getItemViewType(int position) {
                    Message message = getResources().get(position);
                    if (message.getContent() instanceof EventNotificationContent) {
                        return R.layout.item_chat_dialog;
                    } else if (message.getFromUser().getUserName().equals(UserUtil.sUserName)) {
                        return R.layout.item_user_view_talk;
                    } else return R.layout.item_friend_view_talk;
                }

                @Override
                public int getLayoutId(int viewType) {
                    return viewType;
                }

                @Override
                public void bindView(MyHolder holder, int position, Message message) {
                    String text = "";
                    if (message.getContent() instanceof EventNotificationContent) {
                        EventNotificationContent content = (EventNotificationContent) message.getContent();
                        text = content.getEventText();
                        holder.setText(text, R.id.content_wrapper);
                        return;
                    }
                    if (message.getContent() instanceof TextContent) {
                        TextContent content = (TextContent) message.getContent();
                        text = content.getText();
                        String[] split = text.split(",");
                        if (TextUtils.equals(split[0], GroupStudyPrepareFragment.INVITATION_MESSAGE)) {
                            holder.getView(R.id.content_wrapper).setVisibility(View.GONE);
                            holder.getView(R.id.cv).setVisibility(View.GONE);
                            holder.getView(R.id.iv_invitation).setVisibility(View.VISIBLE);
                            String chatRoomId = split[1];
                            holder.getView(R.id.iv_invitation).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //TODO 跳转到聊天室准备界面
                                    Bundle bundle = new Bundle();
                                    bundle.putLong("chatRoomId", Long.parseLong(chatRoomId));
                                    bundle.putInt("time", Integer.parseInt(split[2]));
                                    bundle.putInt("type", 2);
                                    ((MainActivity) getActivity()).getController().navigate(R.id.action_chatFragment_to_groupStudyPrepareFragment, bundle);
                                }
                            });
                            return;
                        }
                    }
                    holder.setText(text, R.id.content_wrapper);
                    ImageView headImg = holder.getView(R.id.img_head);
                    message.getFromUser().getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int i, String s, Bitmap bitmap) {
                            if (i == 0)
                                headImg.setImageBitmap(bitmap);
                        }
                    });
                }
            };
            binding.rvConversation.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvConversation.setAdapter(messageMyRecyclerAdapter);
            binding.rvConversation.scrollToPosition(messageMyRecyclerAdapter.getResources().size() - 1);
        }
    }

    private void addMessage(Message message) {
        messageMyRecyclerAdapter.addResource(message);
        binding.rvConversation.scrollToPosition(messageMyRecyclerAdapter.getResources().size() - 1);
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
        addMessage(message);
        return true;
    }

    public void onBackClick() {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    @Override
    public void onSendClick() {
        String text = binding.etContent.getText().toString();
        binding.etContent.setText("");
        // 发送消息
        if (!sendMessage(text)) {
            MyToast.showMessage("发送失败");
        }
    }
}