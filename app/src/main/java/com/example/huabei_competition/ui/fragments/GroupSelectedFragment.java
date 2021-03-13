package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentGroupSelectedBinding;
import com.example.huabei_competition.databinding.ItemFriendBinding;
import com.example.huabei_competition.db.Label;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.WidgetUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupIDListCallback;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;

public class GroupSelectedFragment extends Fragment {

    private FragmentGroupSelectedBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_selected, container, false);
        binding.setLifecycleOwner(this);
        binding.tvClose.setOnClickListener(this::back);
        initGroupInfoList();
        return binding.getRoot();
    }

    private MyRecyclerAdapter<GroupInfo> groupInfoMyRecyclerAdapter;

    private void back(View v) {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    private void initGroupInfoList() {
        List<GroupInfo> groupInfoList = new ArrayList<>();
        groupInfoMyRecyclerAdapter = new MyRecyclerAdapter<GroupInfo>(groupInfoList) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_friend;
            }

            @Override
            public void bindView(MyHolder holder, int position, GroupInfo groupInfo) {
                ItemFriendBinding binding = (ItemFriendBinding) holder.getBinding();
                File avatarFile = groupInfo.getAvatarFile();
                Bitmap bitmap = WidgetUtil.fileToBitmap(getActivity().getResources(), avatarFile);
                binding.ivThumb.setImageBitmap(bitmap);
                binding.petName.setText(groupInfo.getGroupName());
                binding.itemAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long groupID = groupInfo.getGroupID();
                        String groupName = groupInfo.getGroupName();
                        Label label = new Label(groupName);
                        label.setType(1);
                        label.setGroupId(groupID);
                        DatabaseUtil.saveOrUpdateLabel(label);
                        Log.d(TAG, "onClick: " + label.getLabelName());
                        LiveDataManager.getInstance().with(GroupSelectedFragment.class.getSimpleName()).setValue(label);
                        v.setClickable(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                back(v);
                            }
                        }, 500);
                    }
                });

            }
        };
        binding.myGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.myGroups.setAdapter(groupInfoMyRecyclerAdapter);
        JMessageClient.getGroupIDList(new GetGroupIDListCallback() {
            @Override
            public void gotResult(int i, String s, List<Long> list) {
                for (Long aLong : list) {
                    JMessageClient.getGroupInfo(aLong, new GetGroupInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, GroupInfo groupInfo) {
                            groupInfoList.add(groupInfo);
                            groupInfoMyRecyclerAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

    private static final String TAG = "GroupSelectedFragment";
}