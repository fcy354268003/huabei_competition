package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentGroupInfoBinding;
import com.example.huabei_competition.event.GroupManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyToast;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.RequestCallback;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.GroupMemberInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/2/9
 */
public class GroupInfoFragment extends Fragment {


    private FragmentGroupInfoBinding binding;

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

    private GroupInfo mGroupInfo;

    private void observe() {
        LiveDataManager.getInstance().<GroupInfo>with(getClass().getSimpleName()).observe(getViewLifecycleOwner(), new Observer<GroupInfo>() {
            @Override
            public void onChanged(GroupInfo groupInfo) {
                mGroupInfo = groupInfo;
                JMessageClient.getGroupMembers(groupInfo.getGroupID(), new RequestCallback<List<GroupMemberInfo>>() {
                    @Override
                    public void gotResult(int i, String s, List<GroupMemberInfo> groupMemberInfos) {
                        if (i == 0) {
                            int size = groupMemberInfos.size();
                            binding.tvSum.setText(String.valueOf(size));
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
            }
        });
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