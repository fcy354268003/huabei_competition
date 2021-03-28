package com.example.huabei_competition.ui.fragments;


import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import android.view.animation.RotateAnimation;

import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.FriendsCallback;
import com.example.huabei_competition.databinding.ItemFriendBinding;
import com.example.huabei_competition.db.FriendApply;
import com.example.huabei_competition.db.GroupApply;
import com.example.huabei_competition.db.NPC;
import com.example.huabei_competition.event.EventReceiver;
import com.example.huabei_competition.event.FriendManager;
import com.example.huabei_competition.event.GroupManager;
import com.example.huabei_competition.event.LiveDataManager;

import com.example.huabei_competition.network.api.NPCRel;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.databinding.FragmentFriendsBinding;

import com.example.huabei_competition.ui.activity.TalkActivity;
import com.example.huabei_competition.util.DatabaseUtil;

import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;


import java.io.File;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Create by FanChenYang at 2021/2/17
 */
public class FriendsFragment extends Fragment implements FriendsCallback, LifecycleEventObserver {


    private FragmentFriendsBinding binding;
    private RequestManager glideManager;


    private MyRecyclerAdapter<NPC> NPCadapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null)
                parent.removeView(binding.getRoot());
            return binding.getRoot();
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_friends, container, false);
        glideManager = Glide.with(binding.getRoot());
        binding.setLifecycleOwner(getActivity());

        binding.tvNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity())
                        .getController()
                        .navigate(R.id.action_mainFragment_to_newFriendFragment);
            }
        });
        binding.tvNpcs.setOnClickListener(view -> {
            if (NPCadapter == null) {
                MyToast.showMessage("当前还没有NPC人物");
                return;
            }
            NPCadapter.changeState();
            if (!NPCadapter.isOpen()) {
                binding.ivShow1.setRotation(0);
            } else {
                binding.ivShow1.setRotation(90);
            }
        });
        binding.tvFriends.setOnClickListener(view -> {
            if (friendsAdapter == null)
                return;
            friendsAdapter.changeState();
            if (!friendsAdapter.isOpen()) {
                binding.ivShow2.setRotation(0);
            } else {
                binding.ivShow2.setRotation(90);
            }
        });
        binding.tvGroup.setOnClickListener(view -> {
            Log.d(TAG, "onCreateView: " + groupInfoMyRecyclerAdapter);
            if (groupInfoMyRecyclerAdapter == null)
                return;
            groupInfoMyRecyclerAdapter.changeState();
            if (!groupInfoMyRecyclerAdapter.isOpen()) {
                binding.ivShow3.setRotation(0);
            } else {
                binding.ivShow3.setRotation(90);
            }
        });
        setObserver();
        binding.setCallback(this);
        getNPCsAdapter();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getViewLifecycleOwner().getLifecycle().addObserver(this);
        onRefresh();
    }

    private void setPrompt() {
        int newFriendNotHandle = DatabaseUtil.getNewFriendNotHandle();
        if (newFriendNotHandle != 0) {
            binding.cvUnResolveNew.setVisibility(View.VISIBLE);
            binding.tvUnResolveNew.setText(String.valueOf(newFriendNotHandle));
        }
        if (newFriendNotHandle == 0) {
            binding.cvUnResolveNew.setVisibility(View.GONE);
        }
    }

    public void onRefresh() {
        FriendManager.getFriends(new FriendsListCallback());
        GroupManager.getGroupIdList(new GroupListCallback());
        NPCRel.getNPCList();
        setPrompt();
    }


    /**
     * 设置监听事件
     */
    private void setObserver() {
        observeNewFriend();
        observeNewMessage();
        observeNPC();
    }

    private void observeNPC() {
        LiveDataManager.getInstance().with(FriendsFragment.class.getSimpleName() + "NPC")
                .observe(getViewLifecycleOwner(), new Observer<Object>() {
                    @Override
                    public void onChanged(Object o) {
                        List<NPC> myNPCs = DatabaseUtil.getMyNPCs();
                        Log.d(TAG, "onChanged: " + "购买消息" + myNPCs);
                        LinearLayoutManager manager = new LinearLayoutManager(getContext());
                        binding.rvNpcs.setLayoutManager(manager);
                        Log.d(TAG, "onChanged: " + myNPCs.size());
                        NPCadapter = new MyRecyclerAdapter<NPC>(myNPCs) {
                            @Override
                            public int getLayoutId(int viewType) {
                                return R.layout.item_friend;
                            }

                            @Override
                            public void bindView(MyHolder holder, int position, NPC item) {
                                ImageView imageView = holder.getView(R.id.iv_thumb);
                                imageView.setScaleType(ImageView.ScaleType.FIT_START);
                                glideManager.load(item.getHeadPicture()).into(imageView);
                                holder.getView(R.id.tv_sendTime).setVisibility(View.GONE);
                                holder.setText(item.getName(), R.id.petName);
                                String isDialogue = item.getIsDialogue();
                                if (TextUtils.equals(isDialogue, "true")) {
                                    holder.getView(R.id.cv_prompt).setVisibility(View.VISIBLE);
                                    holder.setText("!", R.id.tv_promptNumber);
                                } else {
                                    holder.getView(R.id.cv_prompt).setVisibility(View.GONE);
                                }
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getContext(), TalkActivity.class);
                                        LiveDataManager.getInstance().with(TalkActivity.class.getSimpleName()).postValue(item);
                                        startActivity(intent);
                                    }
                                });
                            }

                        };
                        binding.rvNpcs.callOnClick();
                        binding.rvNpcs.setAdapter(NPCadapter);
                        NPCadapter.changeState();
                    }
                });
    }

    private void observeNewMessage() {
        MutableLiveData<Message> newMessage = LiveDataManager.getInstance()
                .with(ChatFragment.class.getSimpleName());
        newMessage.observe(getViewLifecycleOwner(), new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
                if (friendsAdapter == null)
                    return;
                String userName = message.getFromUser().getUserName();
                int position = -1;
                List<UserInfo> resources = friendsAdapter.getResources();
                for (int i = 0; i < friendsAdapter.getResources().size(); i++) {
                    UserInfo userInfo = resources.get(i);
                    if (TextUtils.equals(userInfo.getUserName(), userName)) {
                        position = i;
                        break;
                    }
                }
                if (position != -1) {
                    friendsAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    /**
     * 监听新加好友等通知
     */
    private void observeNewFriend() {
        LiveData<FriendApply> applyLiveData = LiveDataManager.getInstance().with(NewFriendsFragment.class.getSimpleName());
        applyLiveData.observe(getViewLifecycleOwner(), new Observer<FriendApply>() {
            @Override
            public void onChanged(FriendApply apply) {
                setPrompt();
            }
        });

        LiveDataManager.getInstance().<GroupApply>with(NewFriendsFragment.class.getSimpleName() + "group").observe(getViewLifecycleOwner(), new Observer<GroupApply>() {
            @Override
            public void onChanged(GroupApply groupApply) {
                setPrompt();
            }
        });
        LiveDataManager.getInstance().with(FriendsFragment.class.getSimpleName() + "groupp").observe(getViewLifecycleOwner(), new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                GroupManager.getGroupIdList(new GroupListCallback());
            }
        });
        LiveDataManager.getInstance().<String>with(EventReceiver.GET_INVITATION).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String o) {
                JMessageClient.getUserInfo(o, new GetUserInfoCallback() {
                    @Override
                    public void gotResult(int i, String s, UserInfo userInfo) {
                        if (i == 0) {
                            FriendManager.getFriends(new FriendsListCallback());
                        }
                    }
                });
            }
        });
    }

    private static final String TAG = "FriendsFragment";

    private void getNPCsAdapter() {
        Log.d(TAG, "getNPCsAdapter: ");
        NPCRel.getNPCList();
    }


    private MyRecyclerAdapter<UserInfo> friendsAdapter;


    @Override
    public void onAddNPCClick() {
        ((MainActivity) getActivity())
                .getController()
                .navigate(R.id.action_mainFragment_to_shopFragment);
    }

    @Override
    public void onAddFriendClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_addFriendFragment);
    }

    @Override
    public void onAddGroupClick() {
        ((MainActivity) getActivity())
                .getController()
                .navigate(R.id.action_mainFragment_to_createStudyRoomFragment);
    }

    @Override
    public void onNewFriendClick() {
        ((MainActivity) getActivity())
                .getController()
                .navigate(R.id.action_mainFragment_to_newFriendFragment);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        Log.d(TAG, "onStateChanged: " + event.name());
    }

    private class FriendsListCallback extends GetUserInfoListCallback {
        @Override
        public void gotResult(int i, String s, List<UserInfo> list) {
            if (list == null)
                list = new ArrayList<>();
            if (i == 0) {
                Log.d(TAG, "gotResult: " + list.size());
                friendsAdapter = new MyRecyclerAdapter<UserInfo>(list) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.item_friend;
                    }

                    @Override
                    public void bindView(MyHolder holder, int position, UserInfo userInfo) {
                        ItemFriendBinding binding = (ItemFriendBinding) holder.getBinding();
                        int unReadMsgCnt = Conversation
                                .createSingleConversation(userInfo.getUserName())
                                .getUnReadMsgCnt();

                        if (unReadMsgCnt != 0) {
                            binding.cvPrompt.setVisibility(View.VISIBLE);
                            binding.tvPromptNumber.setText(String.valueOf(unReadMsgCnt));
                        }
                        // 头像从自己的服务器拉取
                        if (TextUtils.isEmpty(userInfo.getNickname()))
                            binding.petName.setText(userInfo.getUserName());
                        else binding.petName.setText(userInfo.getNickname());
                        binding.itemAll.setOnClickListener(all -> {
                            binding.cvPrompt.setVisibility(View.GONE);
                            Bundle bundle = new Bundle();
                            bundle.putInt(ChatFragment.KEY_TYPE, ChatFragment.PERSON);
                            bundle.putString(ChatFragment.KEY_USERNAME, userInfo.getUserName());
                            ((MainActivity) getActivity())
                                    .getController()
                                    .navigate(R.id.action_mainFragment_to_chatFragment, bundle);
                        });
                    }

                };
                binding.rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

            } else {
                friendsAdapter = new MyRecyclerAdapter<UserInfo>(list) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return 0;
                    }

                    @Override
                    public void bindView(MyHolder holder, int position, UserInfo userInfo) {

                    }
                };
                MyToast.showMessage("获取失败");
            }
            binding.rvFriends.setAdapter(friendsAdapter);
            binding.tvFriends.callOnClick();
        }
    }

    private MyRecyclerAdapter<GroupInfo> groupInfoMyRecyclerAdapter;

    private void initGroupInfoList() {
        if (groupInfos == null) {
            groupInfos = new ArrayList<>();
        }
        groupInfoMyRecyclerAdapter = new MyRecyclerAdapter<GroupInfo>(groupInfos) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_friend;
            }

            @Override
            public void bindView(MyHolder holder, int position, GroupInfo groupInfo) {
                ItemFriendBinding binding = (ItemFriendBinding) holder.getBinding();
                Conversation groupConversation = Conversation
                        .createGroupConversation(groupInfo.getGroupID());
                int unReadMsgCnt = groupConversation.getUnReadMsgCnt();
                if (unReadMsgCnt != 0) {
                    binding.cvPrompt.setVisibility(View.VISIBLE);
                    binding.tvPromptNumber.setText(String.valueOf(unReadMsgCnt));
                }
                File avatarFile = groupInfo.getAvatarFile();
                Bitmap bitmap = WidgetUtil.fileToBitmap(getActivity().getResources(), avatarFile);
                binding.ivThumb.setImageBitmap(bitmap);
                binding.petName.setText(groupInfo.getGroupName());
                binding.content.setText(groupInfo.getGroupDescription());
                binding.itemAll.setOnClickListener(view -> {
                    binding.cvPrompt.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    bundle.putInt(ChatFragment.KEY_TYPE, ChatFragment.GROUP);
                    bundle.putString(ChatFragment.KEY_GROUPId, String.valueOf(groupInfo.getGroupID()));
                    ((MainActivity) getActivity())
                            .getController()
                            .navigate(R.id.action_mainFragment_to_chatFragment, bundle);
                });
            }
        };
        binding.rvGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d(TAG, "initGroupInfoList: 新的");
        binding.rvGroups.setAdapter(groupInfoMyRecyclerAdapter);
        binding.tvGroup.callOnClick();
    }

    private List<GroupInfo> groupInfos;

    private class GroupListCallback extends GetGroupIDListCallback {

        @Override
        public void gotResult(int i, String s, List<Long> list) {
            if (i == 0) {
                groupInfos = new ArrayList<>();
                for (Long aLong : list) {
                    JMessageClient.getGroupInfo(aLong, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                            groupInfos.add(groupInfo);
                            initGroupInfoList();
                        }
                    });
                }
            }
            initGroupInfoList();
        }
    }

}