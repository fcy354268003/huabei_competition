package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.NewFriendsCallback;
import com.example.huabei_competition.databinding.FragmentApplyBinding;
import com.example.huabei_competition.databinding.ItemApplyBinding;
import com.example.huabei_competition.db.FriendApply;
import com.example.huabei_competition.db.GroupApply;
import com.example.huabei_competition.event.EventReceiver;
import com.example.huabei_competition.event.FriendManager;
import com.example.huabei_competition.event.GroupManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.ContactManager;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/2/9
 */
public class NewFriendsFragment extends Fragment implements NewFriendsCallback {


    private FragmentApplyBinding binding;
    private MyRecyclerAdapter<FriendApply> friendApplyAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
            ViewGroup mViewGroup = (ViewGroup) binding.getRoot().getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_apply, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallBack(this);
        initData();
        return binding.getRoot();
    }

    private void initData() {
        initFriendsList();
        initGroupList();
        MutableLiveData<FriendApply> observer = LiveDataManager.getInstance().with(this.getClass().getSimpleName());
        observer.observe(getViewLifecycleOwner(), new Observer<FriendApply>() {
            private boolean isFirst = EventReceiver.getInstance().getIsViscosity(NewFriendsFragment.class.getSimpleName());

            @Override
            public void onChanged(FriendApply friendApply) {
                Log.d(TAG, "onChanged: " + isFirst);
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                if (friendApplyAdapter != null) {
                    List<FriendApply> newFriends = DatabaseUtil.getNewFriends();
                    MyRecyclerAdapter.diff(friendApplyAdapter, friendApplyAdapter.getResources(), newFriends);
                }
            }
        });
        MutableLiveData<GroupApply> groupObserver = LiveDataManager.getInstance().with(this.getClass().getSimpleName() + "group");
        groupObserver.observe(getViewLifecycleOwner(), new Observer<GroupApply>() {
            private boolean isFirst = EventReceiver.getInstance().getIsViscosity(NewFriendsFragment.class.getSimpleName() + "group");

            @Override
            public void onChanged(GroupApply groupApply) {
                Log.d(TAG, "onChanged: " + isFirst);
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                if (friendApplyAdapter != null) {
                    List<GroupApply> newFriends = DatabaseUtil.getNewGroupMembers();
                    MyRecyclerAdapter.diff(groupApplyMyRecyclerAdapter, groupApplyMyRecyclerAdapter.getResources(), newFriends);
                }
            }
        });
    }

    private MyRecyclerAdapter<GroupApply> groupApplyMyRecyclerAdapter;

    private void initGroupList() {
        List<GroupApply> groupApplies = DatabaseUtil.getNewGroupMembers();
        binding.rvGroupsNew.setLayoutManager(new LinearLayoutManager(getContext()));
        groupApplyMyRecyclerAdapter = new MyRecyclerAdapter<GroupApply>(groupApplies) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_apply;
            }

            @Override
            public void bindView(MyHolder holder, int position, GroupApply apply) {
                ItemApplyBinding binding = (ItemApplyBinding) holder.getBinding();
                binding.content.setText("对方留言：" + apply.getReason());
                binding.petName.setText(apply.getUserName());
                ArrayList<String> userNames = new ArrayList<>();
                userNames.add(apply.getUserName());
                if (apply.getState() == 0) {
                    binding.btnConfirm.setOnClickListener(confirm -> {
                        JMessageClient.addGroupMembers(Long.parseLong(apply.getGroupId()), null, userNames, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i != 0) {
                                    MyToast.showMessage("操作失败");
                                } else {
                                    apply.setState(1);
                                    DatabaseUtil.updateNewGroupMemberApply(apply);
                                    bindView(holder, position, apply);
                                }
                            }
                        });

                    });
                    binding.btnReject.setOnClickListener(reject -> {
                        apply.setState(2);
                        DatabaseUtil.updateNewGroupMemberApply(apply);
                        bindView(holder, position, apply);
                    });
                } else if (apply.getState() == 1) {
                    binding.btnReject.setVisibility(View.GONE);
                    binding.btnConfirm.setVisibility(View.GONE);
                    binding.tvState.setVisibility(View.VISIBLE);
                    binding.tvState.setText("已同意");
                } else if (apply.getState() == 2) {
                    binding.tvState.setVisibility(View.VISIBLE);
                    binding.btnReject.setVisibility(View.GONE);
                    binding.btnConfirm.setVisibility(View.GONE);
                    binding.tvState.setText("已拒绝");
                }
            }
        };
        binding.rvGroupsNew.setAdapter(groupApplyMyRecyclerAdapter);
    }

    private void initFriendsList() {
        List<FriendApply> newFriends = DatabaseUtil.getNewFriends();

        binding.rvFriendsNew.setLayoutManager(new LinearLayoutManager(getContext()));
        friendApplyAdapter = new MyRecyclerAdapter<FriendApply>(newFriends) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_apply;
            }

            @Override
            public void bindView(MyHolder holder, int position, FriendApply friendApply) {
                ItemApplyBinding binding = (ItemApplyBinding) holder.getBinding();
                binding.content.setText("对方留言：" + friendApply.getReason());
                binding.petName.setText(friendApply.getUserName());
                if (friendApply.getState() == 0) {
                    binding.btnConfirm.setOnClickListener(confirm -> {
                        ContactManager.acceptInvitation(friendApply.getUserName(), friendApply.getAppKey(), new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i != 0) {
                                    MyToast.showMessage("操作失败");
                                } else {
                                    friendApply.setState(1);
                                    DatabaseUtil.updateNewFriendApply(friendApply);
                                    bindView(holder, position, friendApply);
                                }
                            }
                        });

                    });
                    binding.btnReject.setOnClickListener(reject -> {

                        ContactManager.declineInvitation(friendApply.getUserName(), friendApply.getAppKey(), "", new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i != 0) {
                                    MyToast.showMessage("操作失败");
                                } else {
                                    friendApply.setState(2);
                                    DatabaseUtil.updateNewFriendApply(friendApply);
                                    bindView(holder, position, friendApply);
                                }
                            }
                        });

                    });
                } else if (friendApply.getState() == 1) {
                    binding.btnReject.setVisibility(View.GONE);
                    binding.btnConfirm.setVisibility(View.GONE);
                    binding.tvState.setVisibility(View.VISIBLE);
                    binding.tvState.setText("已同意");
                } else if (friendApply.getState() == 2) {
                    binding.tvState.setVisibility(View.VISIBLE);
                    binding.btnReject.setVisibility(View.GONE);
                    binding.btnConfirm.setVisibility(View.GONE);
                    binding.tvState.setText("已拒绝");
                }
            }
        };
        binding.rvFriendsNew.setAdapter(friendApplyAdapter);
    }

    private static final String TAG = "----------------";


    @Override
    public void onApplyClick() {
        final CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.apply_fragment_dialog);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                EditText reason = rootView.findViewById(R.id.et_reason);
                EditText userName = rootView.findViewById(R.id.et_userInfo);
                rootView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String res = reason.getText().toString();
                        final String user = userName.getText().toString();
                        final String replace = user.replace(" ", "");
                        // 调用添加好友的办法
                        FriendManager.sendInvitationRequest(user, null, replace, new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                Log.d(TAG, "gotResult: " + s);
                                if (i == 0) {
                                    MyToast.showMessage("请求发送成功");
                                } else {
                                    GroupManager.applyToGroup(Long.parseLong(replace), res, new BasicCallback() {
                                        @Override
                                        public void gotResult(int i, String s) {
                                            Log.d(TAG, "gotResult: " + s);
                                            if (i != 0)
                                                MyToast.showMessage(s);
                                            else MyToast.showMessage("请求已发送");
                                        }
                                    });
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
    public void onBackClick() {
        ((MainActivity) getActivity()).getController().navigateUp();

    }


}