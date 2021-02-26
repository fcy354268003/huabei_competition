package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentManagerGroupMemberBinding;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class ManagerGroupMemberFragment extends Fragment {

    private FragmentManagerGroupMemberBinding binding;
    private long groupId;
    private GroupInfo mGroupInfo;
    private String groupSize;
    private MyRecyclerAdapter<UserInfo> recyclerAdapter;

    private void initArgs() {
        Bundle bundle = getArguments();
        groupId = bundle.getLong("groupId");
        groupSize = bundle.getString("groupSize");
        JMessageClient.getGroupInfo(groupId, new GetGroupInfoCallback() {
            @Override
            public void gotResult(int i, String s, GroupInfo groupInfo) {
                if (i == 0) {
                    mGroupInfo = groupInfo;
                    initAdapter();
                    initData();
                } else {
                    MyToast.showMessage("获取信息失败");
                }
            }
        });
    }

    private void initAdapter() {
        List<GroupMemberInfo> groupMemberInfos = mGroupInfo.getGroupMemberInfos();
        for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
            UserInfo userInfo = groupMemberInfo.getUserInfo();
            if (!userInfo.getUserName().equals(UserUtil.sUserName))
                userInfos.add(userInfo);
        }
        fillData();
        binding.rvMembers.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private final List<UserInfo> userInfos = new ArrayList<>();

    private void fillData() {
        recyclerAdapter = new MyRecyclerAdapter<UserInfo>(userInfos) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_group_members;
            }

            @Override
            public void bindView(MyHolder holder, int position, UserInfo o) {
                ImageView portrait = holder.getView(R.id.iv_portrait);
                o.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0) {
                            portrait.setImageBitmap(bitmap);
                        }
                    }
                });
                String currentTime = o.getSignature();
                if (!TextUtils.isEmpty(currentTime)) {
                    holder.setText(currentTime, R.id.tv_currentStudyTime);
                }
                String nickname = o.getNickname();
                if (!TextUtils.isEmpty(nickname)) {
                    holder.setText(nickname, R.id.tv_nickName);
                } else {
                    holder.setText(o.getUserName(), R.id.tv_nickName);
                }
                holder.getView(R.id.btn_driveAway).setOnClickListener(view -> {
                    String userName = o.getUserName();
                    JMessageClient.removeGroupMembers(groupId, Collections.singletonList(userName), new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Snackbar.make(getView(), "踢出成员成功", Snackbar.LENGTH_LONG).show();
                                recyclerAdapter.getResources().remove(o);
                                recyclerAdapter.notifyDataSetChanged();
                            } else {
                                Snackbar.make(getView(), "踢出成员失败", Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                });
            }

        };
        binding.rvMembers.setAdapter(recyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup group = (ViewGroup) binding.getRoot().getParent();
            if (group != null) {
                group.removeView(binding.getRoot());
                return binding.getRoot();
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_manager_group_member, container, false);
        initArgs();
        binding.setLifecycleOwner(this);
        binding.ivBack.setOnClickListener(this::back);
        return binding.getRoot();
    }

    private void back(View view) {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    private static final String TAG = "ManagerGroupMemberFragm";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initData() {
        mGroupInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                Log.d(TAG, "gotResult: " + s);
                if (i == 0) {
                    binding.ivGroupAvatar.setImageBitmap(bitmap);
                }
            }
        });
        binding.tvGroupName.setText(mGroupInfo.getGroupName());
        String name;
        name = mGroupInfo.getOwnerMemberInfo().getNickName();
        if (TextUtils.isEmpty(name)) {
            name = mGroupInfo.getOwnerMemberInfo().getUserInfo().getUserName();
        }
        binding.tvCreator.setText(name);
        binding.tvSum.setText(groupSize);
    }
}