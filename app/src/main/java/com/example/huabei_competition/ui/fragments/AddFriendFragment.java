package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.base.BaseFragment;
import com.example.huabei_competition.databinding.FragmentAddFriendBinding;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupBasicInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;


public class AddFriendFragment extends BaseFragment {

    private FragmentAddFriendBinding binding;
    private final ListRefresher refresher = new ListRefresher();

    /**
     * 设置点击事件，在父类onCreateView中回调
     */
    @Override
    protected void setListener() {
        binding = getBinding(FragmentAddFriendBinding.class);
        binding.ivRefresh.setOnClickListener(v -> refresher.getUsers());
        binding.ivBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).getController().navigateUp();
        });
        binding.button3.setOnClickListener(v -> {
            String userName = binding.editText.getText().toString();
            userName = userName.replace(" ", "");
            JMessageClient.getUserInfo(userName, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0) {
                        toUserDetail(userInfo.getUserName());
                    } else {
                        MyToast.showMessage("没有查找到目标用户");
                    }
                }
            });
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
        refresher.getUsers();
    }

    @Override
    public void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        refresher.getUsers();
    }

    @Override
    protected int setLayoutID() {
        return R.layout.fragment_add_friend;
    }

    /**
     * @param userName 用户昵称
     *                 跳转到用户详情页
     */
    private void toUserDetail(String userName) {
        LiveDataManager.getInstance().with(DataShowFragment.class.getSimpleName()).setValue(userName);
        ((MainActivity) getActivity()).getController().navigate(R.id.action_addFriendFragment_to_dataShowFragment);
    }

    private void initAdapter() {
        ArrayList<UserInfo> userInfos = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvRecommendFriend.setLayoutManager(linearLayoutManager);
        binding.rvRecommendFriend.setAdapter(new MyRecyclerAdapter<UserInfo>(userInfos) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_friend;
            }

            @Override
            public void bindView(MyHolder holder, int position, UserInfo o) {
                String userName = o.getUserName();
                String nickname = o.getNickname();
                o.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0) {
                            ImageView thumb = holder.getView(R.id.iv_thumb);
                            thumb.setImageBitmap(bitmap);
                        }
                    }
                });
                holder.setText(nickname, R.id.petName);
                holder.setText("id:" + userName, R.id.content);
                holder.getView(R.id.btn_join).setVisibility(View.VISIBLE);
                holder.getView(R.id.btn_join).setOnClickListener(view -> {
                    toUserDetail(userName);
                });
            }

        });
    }

    private static final String TAG = "AddFriendFragment";

    private class ListRefresher {
        private boolean is;
        private Map<String, UserInfo> al;
        private int start = 0;

        private void changeList(List<UserInfo> users) {
            start += 2;
            start = start % 11;
            Log.d(TAG, "changeList: " + users.size());
            MyRecyclerAdapter<UserInfo> userInfoMyRecyclerAdapter = (MyRecyclerAdapter<UserInfo>) binding.rvRecommendFriend.getAdapter();
            List<UserInfo> resources = userInfoMyRecyclerAdapter.getResources();
            resources.clear();
            for (UserInfo user : users) {
                Log.d(TAG, "changeList: " + user.getUserName());
                resources.add(user);
                userInfoMyRecyclerAdapter.notifyDataSetChanged();
            }
        }

        private void getUsers() {
            al = new HashMap<>();
            is = true;
            JMessageClient.getPublicGroupListByApp(null, start, 11, new RequestCallback<List<GroupBasicInfo>>() {
                @Override
                public void gotResult(int i, String s, List<GroupBasicInfo> groupBasicInfos) {
                    for (GroupBasicInfo groupBasicInfo : groupBasicInfos) {
                        if (!is)
                            return;
                        long groupID = groupBasicInfo.getGroupID();
                        JMessageClient.getGroupMembers(groupID, new RequestCallback<List<GroupMemberInfo>>() {
                            @Override
                            public void gotResult(int i, String s, List<GroupMemberInfo> groupMemberInfos) {
                                Log.d(TAG, "gotResult: " + s);
                                if (i == 0) {
                                    for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
                                        UserInfo userInfo = groupMemberInfo.getUserInfo();
                                        Log.d(TAG, "gotResult: " + userInfo.getUserName());
                                        if (!userInfo.isFriend() && !al.containsKey(userInfo.getUserName())) {
                                            Log.d(TAG, "gotResult: " + userInfo.getUserName());
                                            al.put(userInfo.getUserName(), userInfo);
                                        }
                                        Log.d(TAG, "gotResult: " + al.size());
                                        if (al.size() == 3) {
                                            is = !is;
                                            ArrayList<UserInfo> all = new ArrayList<>();
                                            Set<String> set = al.keySet();
                                            for (String s1 : set) {
                                                all.add(al.get(s1));
                                            }
                                            changeList(all);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }


}