package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentGroupStudyPrepareBinding;
import com.example.huabei_competition.event.ChatRoomUtil;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;


import java.io.IOException;
import java.util.ArrayList;


import java.util.Collections;
import java.util.List;


import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.model.ChatRoomInfo;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/22
 * <p>
 * 群组自习室 准备阶段
 * </p>
 */
public class GroupStudyPrepareFragment extends Fragment {
    private FragmentGroupStudyPrepareBinding binding;
    private MyRecyclerAdapter<UserInfo> myRecyclerAdapter;
    private List<UserInfo> userInfos = new ArrayList<>();
    private Conversation chatRoomConversation;
    private long invitationGroupID;
    private int time;
    private Long chatRoomId;
    // 0:房主
    private int type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding != null) {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
            ViewGroup mViewGroup = (ViewGroup) binding.getRoot().getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_study_prepare, container, false);
        binding.ivBack.setOnClickListener(this::back);
        binding.btnStart.setOnClickListener(this::start);
        return binding.getRoot();
    }

    private void start(View view) {
        if (userInfos.size() >= 2) {
            // TODO 向所有用户发送一个 开始 的透传消息 跳转到学习页面
            sendCMD(START_EVENT);
            toNextFragment();
        } else {
            MyToast.showMessage("人数大于等于3个才可以开始哦！");
        }
    }


    private void back(View v) {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void initData() {
        initAdapter();
        Bundle arguments = getArguments();
        chatRoomId = arguments.getLong("chatRoomId");
        time = arguments.getInt("time");
        int type = arguments.getInt("type");
        if (type == 2) {
            touristAction();
        } else {
            creatorAction();
        }
        setObserver();
        binding.tvTimeLeft.setText(String.valueOf(time));
    }

    public static final String STATE_ING = "isGoing";

    private void setObserver() {
        LiveDataManager.getInstance().<CommandNotificationEvent>with(GroupStudyPrepareFragment.class.getSimpleName()).observe(getViewLifecycleOwner(), new Observer<CommandNotificationEvent>() {
            @Override
            public void onChanged(CommandNotificationEvent o) {
                Log.d(TAG, "onChanged: livedata收到透传消息" + o.getMsg());
                if (o.getMsg().equals(ADD_EVENT) || o.getMsg().equals(LEAVE_EVENT)) {
                    getUserList();
                    if (type == 0) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                check();
                            }
                        }, 500);
                    }
                }
                if (o.getMsg().equals(START_EVENT)) {
                    toNextFragment();
                }

            }
        });
    }

    private void check() {
        if (userInfos.size() >= 2) {
            binding.btnStart.setBackgroundResource(R.color.orange);
        }
    }

    /**
     * 跳转到下一页面
     */
    private void toNextFragment() {
        if (type == 0)
            ChatRoomUtil.updateRoomState(String.valueOf(chatRoomId), UserUtil.sUserName, STATE_ING, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showMessage("网络出现异常");
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bundle bundle = new Bundle();
                                bundle.putInt("time", time);
                                LiveDataManager.getInstance().with(GroupStudyFragment.class.getSimpleName()).postValue(userInfos);
                                ((MainActivity) (getActivity())).getController().navigate(R.id.action_groupStudyPrepareFragment_to_groupStudyFragment, bundle);
                            }
                        });

                    }
                }
            });
    }

    /**
     * 加入聊天室的动作
     */
    private void touristAction() {
        type = 1;
        ChatRoomManager.getChatRoomInfos(Collections.singleton(chatRoomId), new RequestCallback<List<ChatRoomInfo>>() {
            @Override
            public void gotResult(int i, String s, List<ChatRoomInfo> chatRoomInfos) {
                if (i == 0) {
                    ChatRoomInfo chatRoomInfo = chatRoomInfos.get(0);
                    String description = chatRoomInfo.getDescription();
                    if (TextUtils.equals(description, STATE_ING)) {
                        back(getView());
                    }
                } else {
                    back(getView());
                }
            }
        });
        binding.btnStart.setVisibility(View.GONE);
        ChatRoomManager.enterChatRoom(chatRoomId, new RequestCallback<Conversation>() {
            @Override
            public void gotResult(int i, String s, Conversation conversation) {
                if (i != 0) {
                    MyToast.showMessage("进入自习室失败");
                    back(binding.getRoot());
                } else {
                    Log.d(TAG, "gotResult: 成功进入");
                    getUserList();
                    chatRoomConversation = conversation;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendCMD(ADD_EVENT);
                        }
                    }, 1000);
                }
            }

        });
    }

    private void sendCMD(String content) {
        Log.d(TAG, "sendCMD: " + userInfos.size());
        for (UserInfo userInfo : userInfos) {
            JMessageClient.sendSingleTransCommand(userInfo.getUserName(), null, content, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Log.d(TAG, "gotResult: 发送透传消息" + s);
                }
            });
        }
    }

    public static final String START_EVENT = "0x222";
    public static final String ADD_EVENT = "0x111";
    public static final String LEAVE_EVENT = "0x333";

    /**
     * 创建聊天室的动作
     */
    private void creatorAction() {
        type = 0;
        Bundle arguments = getArguments();
        invitationGroupID = Long.parseLong(arguments.getString("groupId"));
        // 向群里发一条 邀请  信息
        Message groupTextMessage = JMessageClient.createGroupTextMessage(invitationGroupID, INVITATION_MESSAGE + "," + chatRoomId + "," + time);
        JMessageClient.sendMessage(groupTextMessage);
        getUserList();
    }

    public static final String INVITATION_MESSAGE = "0x11111asdasd";

    // 刷新用户列表
    private void getUserList() {
        userInfos.clear();
        ChatRoomUtil.getChatRoomUserList(chatRoomId, new ChatRoomUtil.CallBack() {
            @Override
            public void onListCallBack(List<String> userIds) {
                for (String userId : userIds) {
                    JMessageClient.getUserInfo(userId, new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0) {
                                userInfos.add(userInfo);
                                myRecyclerAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            }
        });
    }

    private void initAdapter() {
        myRecyclerAdapter = new MyRecyclerAdapter<UserInfo>(userInfos) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_study_recyclerview;
            }

            @Override
            public void bindView(MyHolder holder, int position, UserInfo userInfo) {
                ImageView avatarView = holder.getView(R.id.iv_memberAvatar);
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        avatarView.setImageBitmap(bitmap);
                    }
                });
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvMembers.setLayoutManager(linearLayoutManager);
        binding.rvMembers.setAdapter(myRecyclerAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ChatRoomManager.leaveChatRoom(chatRoomId, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "onDestroyViewgotResult: " + s);
            }
        });
        sendCMD(LEAVE_EVENT);
    }

    private static final String TAG = "GroupStudyPrepareFragme";
}