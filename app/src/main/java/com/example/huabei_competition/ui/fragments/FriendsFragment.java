package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.FriendsCallback;
import com.example.huabei_competition.databinding.ItemFriendBinding;
import com.example.huabei_competition.db.FriendApply;
import com.example.huabei_competition.db.GroupApply;
import com.example.huabei_competition.event.FriendManager;
import com.example.huabei_competition.event.GroupManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.databinding.FragmentFriendsBinding;
import com.example.huabei_competition.db.Item;
import com.example.huabei_competition.ui.activity.TalkActivity;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.callback.GetUserInfoListCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/2/17
 */
public class FriendsFragment extends Fragment implements FriendsCallback {

    private SharedPreferences userData;
    private FragmentFriendsBinding binding;

    private boolean[] isClose = new boolean[]{true, true, true};
    public int[] icons = new int[]{
            R.drawable.head1,
            R.drawable.head2,
            R.drawable.head3,
            R.drawable.head4};
    public String[] names = new String[]{"李白", "杜甫", "苏轼", "花木兰"};
    public String[] briefIntroduce;
    private MyRecyclerAdapter<Item> NPCadapter;
    private int currentSum = 0;

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
        userData = getActivity().getSharedPreferences("userData"
                , Context.MODE_PRIVATE);
        briefIntroduce = getActivity().getResources()
                .getStringArray(R.array.briefIntroduction);
        binding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_friends, container, false);

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
            NPCadapter.changeState();
            isClose[0] = !isClose[0];
        });
        binding.tvFriends.setOnClickListener(view -> {
            friendsAdapter.changeState();
            isClose[1] = !isClose[1];
        });
        binding.tvGroup.setOnClickListener(view -> {
            groupInfoMyRecyclerAdapter.changeState();
            isClose[2] = !isClose[2];
        });
        setObserver();
        binding.setCallback(this);
        binding.rvNpcs.setAdapter(getNPCsAdapter(binding));
        return binding.getRoot();
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
        setPrompt();
    }

    @Override
    public void onStart() {
        super.onStart();
        onRefresh();
    }

    /**
     * 设置监听事件
     */
    private void setObserver() {
        observeNewFriend();
        observeNewMessage();
    }

    private void observeNewMessage() {
        MutableLiveData<Message> newMessage = LiveDataManager.getInstance()
                .with(ChatFragment.class.getSimpleName());
        newMessage.observe(getViewLifecycleOwner(), new Observer<Message>() {
            @Override
            public void onChanged(Message message) {
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
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    private static final String TAG = "FriendsFragment";

    /**
     * @param binding binding
     * @return 构造好的friends adapter
     */
    private MyRecyclerAdapter<Item> getNPCsAdapter(FragmentFriendsBinding binding) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            Item item = new Item();
            item.setName(names[i]);
            item.setBrief(briefIntroduce[i]);
            items.add(item);
        }
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.rvNpcs.setLayoutManager(manager);
        NPCadapter = new MyRecyclerAdapter<Item>(items) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_friend;
            }

            @Override
            public void bindView(MyHolder holder, int position, Item item) {
                ImageView imageView = holder.getView(R.id.iv_thumb);
                imageView.setImageResource(icons[position]);
                holder.getView(R.id.tv_sendTime).setVisibility(View.GONE);
                holder.setText(item.getName(), R.id.petName);
                holder.setText(briefIntroduce[position], R.id.content);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), TalkActivity.class);
                        startActivity(intent);
                    }
                });
            }

        };
        if (isClose[0])
            NPCadapter.changeState();
        return NPCadapter;
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
        // 添加好友
        final CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.apply_fragment_dialog);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                EditText reason = rootView.findViewById(R.id.et_reason);
                EditText userName = rootView.findViewById(R.id.et_userInfo);
                rootView.findViewById(R.id.et_reason);
                rootView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String res = reason.getText().toString();
                        final String user = userName.getText().toString();
                        // 调用添加好友的办法
                        FriendManager.sendInvitationRequest(user, null, res, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                Log.d(TAG, "gotResult: " + s);
                                if (i == 0) {
                                    MyToast.showMessage("请求发送成功");
                                } else {
                                    MyToast.showMessage("请求发送失败");
                                }
                            }
                        });
                        customerDialog.dismiss();
                    }
                });
                rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customerDialog.dismiss();
                    }
                });
            }
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "apply");
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
                binding.rvFriends.setAdapter(friendsAdapter);
                if (isClose[1])
                    friendsAdapter.changeState();
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
        binding.rvGroups.setAdapter(groupInfoMyRecyclerAdapter);
        if (isClose[2])
            groupInfoMyRecyclerAdapter.changeState();
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
                        }
                    });
                }
            }
            initGroupInfoList();
        }
    }

}