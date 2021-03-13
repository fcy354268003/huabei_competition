package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentAddFriendBinding;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;

import java.util.ArrayList;
import java.util.List;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;


public class AddFriendFragment extends Fragment {

    private FragmentAddFriendBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_friend, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        initAdapter();
        binding.ivRefresh.setOnClickListener(view -> getUsers());
        binding.ivBack.setOnClickListener(view -> {
            ((MainActivity) getActivity()).getController().navigateUp();
        });
        binding.button3.setOnClickListener(view -> {
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
        return binding.getRoot();
    }

    private void toUserDetail(String userName) {
        LiveDataManager.getInstance().with(DataShowFragment.class.getSimpleName()).setValue(userName);
        ((MainActivity) getActivity()).getController().navigate(R.id.action_addFriendFragment_to_dataShowFragment);
    }

    private void initAdapter() {
        ArrayList<UserInfo> userInfos = new ArrayList<>();
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

    private void changeList(List<String> users) {
        MyRecyclerAdapter<UserInfo> userInfoMyRecyclerAdapter = (MyRecyclerAdapter<UserInfo>) binding.rvRecommendFriend.getAdapter();
        List<UserInfo> resources = userInfoMyRecyclerAdapter.getResources();
        resources.clear();
        for (String user : users) {
            JMessageClient.getUserInfo(user, new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0) {
                        resources.add(userInfo);
                        userInfoMyRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void getUsers() {

    }

}