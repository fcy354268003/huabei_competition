package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentWeChatBinding;
import com.example.huabei_competition.widget.MyFragmentAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by FanChenYang at 2021/2/20
 */
public class WeChatFragment extends Fragment implements TabLayout.OnTabSelectedListener {


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        fragments.add(new FriendsFragment());
        fragments.add(new CommentFragment());
        titles.add("好友");
        titles.add("动态");
    }
    public void onRefresh(){
        FriendsFragment friendsFragment = (FriendsFragment) fragments.get(0);
        friendsFragment.onRefresh();
    }
    private FragmentWeChatBinding binding;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_we_chat, container, false);
        binding.setLifecycleOwner(this);
        binding.vpChat.setAdapter(new MyFragmentAdapter(getActivity().getSupportFragmentManager()
                , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments, titles));
        binding.tabsChat.setupWithViewPager(binding.vpChat);

        return binding.getRoot();
    }

    private static final String TAG = "WeChatFragment";

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        binding.vpChat.setCurrentItem(position);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}