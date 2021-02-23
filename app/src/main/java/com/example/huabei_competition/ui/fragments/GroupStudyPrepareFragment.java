package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentGroupStudyPrepareBinding;
import com.example.huabei_competition.event.ChatRoomUtil;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import cn.jpush.im.android.api.ChatRoomManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
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
        return binding.getRoot();
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
        Bundle arguments = getArguments();
        chatRoomId = arguments.getLong("chatRoomId");
        time = arguments.getInt("time");
        int type = arguments.getInt("type");
        if (type == 2) {
            touristAction();
        } else {
            creatorAction();
        }
        binding.tvTimeLeft.setText(String.valueOf(time));
        getUserList();
    }

    /**
     * 加入聊天室的动作
     */
    private void touristAction() {
        ChatRoomManager.enterChatRoom(chatRoomId, new RequestCallback<Conversation>() {
            @Override
            public void gotResult(int i, String s, Conversation conversation) {
                Log.d(TAG, "gotResult: " + s);
                if (i != 0) {
                    MyToast.showMessage("进入自习室失败");
                    back(binding.getRoot());
                    chatRoomConversation = conversation;
                }
            }
        });
    }

    /**
     * 创建聊天室的动作
     */
    private void creatorAction() {
        Bundle arguments = getArguments();
        invitationGroupID = Long.parseLong(arguments.getString("groupId"));
        // 向群里发一条 邀请  信息
        Message groupTextMessage = JMessageClient.createGroupTextMessage(invitationGroupID, INVITATION_MESSAGE + "," + chatRoomId + "," + time);
        JMessageClient.sendMessage(groupTextMessage);
    }

    public static final String INVITATION_MESSAGE = "0x11111asdasd";

    private void getUserList() {
        initAdapter();
        ChatRoomUtil.getChatRoomUserList(chatRoomId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(TAG, "onResponse: " + "获取用户列表");
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        int total = jsonObject.getInt("total");
                        JSONArray users = jsonObject.getJSONArray("users");
                        for (int j = 0; j < total; j++) {
                            JSONObject userJSONObject = users.getJSONObject(j);
                            String userId = userJSONObject.getString("username");
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Log.d(TAG, "onResponse: " + response.body().string());
                response.close();
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
    public void onDestroy() {
        super.onDestroy();
        ChatRoomManager.leaveChatRoom(chatRoomId, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: " + s);
            }
        });
    }

    private static final String TAG = "GroupStudyPrepareFragme";
}