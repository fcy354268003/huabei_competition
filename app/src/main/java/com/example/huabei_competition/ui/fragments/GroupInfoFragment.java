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

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentGroupInfoBinding;
import com.example.huabei_competition.event.GroupManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.google.android.material.snackbar.Snackbar;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/2/9
 */
public class GroupInfoFragment extends Fragment {


    private FragmentGroupInfoBinding binding;
    private List<GroupMemberInfo> groupMemberInfos;
    private int groupSize;
    private GroupInfo mGroupInfo;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_info, container, false);
        binding.setLifecycleOwner(this);
        observe();
        binding.btnApply.setOnClickListener(this::onApplyClick);
        binding.ivBack.setOnClickListener(this::onBackClick);
        return binding.getRoot();
    }

    private void onBackClick(View view) {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initAdapter() {
        if (groupMemberInfos == null || groupMemberInfos.size() == 0) {
            groupMemberInfos = mGroupInfo.getGroupMemberInfos();
        }
        MyRecyclerAdapter<GroupMemberInfo> memberInfoMyRecyclerAdapter = new MyRecyclerAdapter<GroupMemberInfo>(groupMemberInfos) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.portrait_group_member;
            }

            @Override
            public void bindView(MyHolder holder, int position, GroupMemberInfo groupMemberInfo) {
                UserInfo userInfo = groupMemberInfo.getUserInfo();
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0) {
                            ImageView imageView = holder.getView(R.id.portrait);
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });
                String name;
                name = userInfo.getNickname();
                if (TextUtils.isEmpty(name)) {
                    name = userInfo.getUserName();
                }
                holder.setText(name, R.id.name);

            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvMembers.setLayoutManager(linearLayoutManager);
        binding.rvMembers.setAdapter(memberInfoMyRecyclerAdapter);
    }

    private static final String TAG = "GroupInfoFragment";

    private void observe() {
        LiveDataManager.getInstance().<GroupInfo>with(getClass().getSimpleName()).observe(getViewLifecycleOwner(), new Observer<GroupInfo>() {
            @Override
            public void onChanged(GroupInfo groupInfo) {
                mGroupInfo = groupInfo;
                long joinGroupTime = mGroupInfo.getOwnerMemberInfo().getJoinGroupTime();
                Calendar instance = Calendar.getInstance();
                Date date = new Date(joinGroupTime);
                instance.setTime(date);
                if (!checkManager())
                    checkMember();
                instance.setTime(date);
                String createTime = "(创建于" + instance.get(Calendar.YEAR) + "," + (instance.get(Calendar.MONTH) + 1) + "," + instance.get(Calendar.DAY_OF_MONTH) + ")";
                binding.tvCreateTime.setText(createTime);
                binding.groupID.setText(String.valueOf(mGroupInfo.getGroupID()));
                JMessageClient.getGroupMembers(groupInfo.getGroupID(), new RequestCallback<List<GroupMemberInfo>>() {
                    @Override
                    public void gotResult(int i, String s, List<GroupMemberInfo> groupMemberInfos) {
                        if (i == 0) {
                            groupSize = groupMemberInfos.size();
                            binding.tvSum.setText(String.valueOf(groupSize));
                            String nickname = groupInfo.getOwnerMemberInfo().getUserInfo().getNickname();
                            String userName = groupInfo.getOwnerMemberInfo().getUserInfo().getUserName();
                            binding.tvCreator.setText(TextUtils.isEmpty(nickname) ? userName : nickname);
                            binding.tvGroupName.setText(groupInfo.getGroupName());
                            binding.tvDes.setText(groupInfo.getGroupDescription());
                            groupInfo.getBigAvatarBitmap(new GetAvatarBitmapCallback() {
                                @Override
                                public void gotResult(int i, String s, Bitmap bitmap) {
                                    if (i == 0) {
                                        binding.ivGroupAvatar.setImageBitmap(bitmap);
                                    } else MyToast.showMessage("获取信息异常");
                                }
                            });
                            long userID = JMessageClient.getMyInfo().getUserID();
                            for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
                                if (groupMemberInfo.getUid() == userID) {
                                    binding.btnApply.setVisibility(View.GONE);
                                    return;
                                }
                            }
                        } else {
                            MyToast.showMessage("信息获取异常");
                        }
                    }
                });
                initAdapter();
            }
        });
    }

    private void checkMember() {
        groupMemberInfos = mGroupInfo.getGroupMemberInfos();
        for (GroupMemberInfo groupMemberInfo : groupMemberInfos) {
            if (groupMemberInfo.getUserInfo().getUserName().equals(UserUtil.sUserName)) {
                //TODO 初始化
                binding.btnLeaveGroup.setVisibility(View.VISIBLE);
                binding.btnLeaveGroup.setOnClickListener(view -> {
                    List<String> ids = new ArrayList<>();
                    ids.add(UserUtil.sUserName);
                    JMessageClient.removeGroupMembers(mGroupInfo.getGroupID(), null, ids, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Snackbar.make(getView(), "推出群成功", Snackbar.LENGTH_LONG);
                                ((MainActivity) getActivity()).getController().navigate(R.id.action_groupInfoFragment_to_mainFragment);
                            }
                        }
                    });
                });
            }

        }
    }

    private boolean checkManager() {
        if (mGroupInfo.getOwnerMemberInfo().getUserInfo().getUserName().equals(UserUtil.sUserName)) {
            binding.btnManageMember.setVisibility(View.VISIBLE);
            binding.btnManageMember.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                bundle.putString("groupSize", String.valueOf(groupSize));
                bundle.putLong("groupId", mGroupInfo.getGroupID());
                ((MainActivity) getActivity()).getController().navigate(R.id.action_groupInfoFragment_to_managerGroupMemberFragment, bundle);
            });
            binding.btnLeaveGroup.setVisibility(View.VISIBLE);
            binding.btnLeaveGroup.setOnClickListener(view -> {
                List<String> ids = new ArrayList<>();
                ids.add(UserUtil.sUserName);
                JMessageClient.removeGroupMembers(mGroupInfo.getGroupID(), null, ids, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            Snackbar.make(getView(), "推出群成功", Snackbar.LENGTH_LONG);
                            ((MainActivity) getActivity()).getController().navigate(R.id.action_groupInfoFragment_to_mainFragment);
                        }
                    }
                });
            });
            return true;
        }
        return false;
    }

    private void onApplyClick(View v) {
        GroupManager.applyToGroup(mGroupInfo.getGroupID(), "申请加入" + mGroupInfo.getGroupName() + "自习室", new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    MyToast.showMessage("申请已发送");
                } else MyToast.showMessage("申请失败");
            }
        });
    }

}