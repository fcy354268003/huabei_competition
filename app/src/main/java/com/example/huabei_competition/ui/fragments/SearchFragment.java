package com.example.huabei_competition.ui.fragments;

import android.content.pm.FeatureGroupInfo;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentSearchBinding;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyToast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetGroupInfoCallback;
import cn.jpush.im.android.api.model.GroupInfo;

/**
 * Create by FanChenYang at 2021/2/26
 */
public class SearchFragment extends Fragment {


    private FragmentSearchBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        binding.setLifecycleOwner(this);
        binding.setMContext(this);
        return binding.getRoot();
    }

    public void onBackPress() {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    public void onSearchPress() {
        String id = binding.etGroupId.getText().toString();
        id = id.replace(" ", "");
        if (!TextUtils.isEmpty(id))
            JMessageClient.getGroupInfo(Long.parseLong(id), new GetGroupInfoCallback() {
                @Override
                public void gotResult(int i, String s, GroupInfo groupInfo) {
                    if (i == 0) {
                        ((MainActivity) getActivity()).getController().navigate(R.id.action_searchFragment_to_groupInfoFragment);
                        LiveDataManager.getInstance().with(GroupInfoFragment.class.getSimpleName()).postValue(groupInfo);
                    } else {
                        MyToast.showMessage("没有查找到相关群聊");
                    }
                }
            });
    }
}