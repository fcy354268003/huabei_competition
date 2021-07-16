package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentMainBinding;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.ui.mine.MineFragment;
import com.example.huabei_competition.widget.MyFragmentAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by FanChenYang at 2021/2/9
 */
public class MainFragment extends Fragment implements ViewPager.OnPageChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragments = new ArrayList<>();
        fragments.add(new WelcomeFragment());
        fragments.add(new WeChatFragment());
        fragments.add(new SelfRoomFragment());
        fragments.add(new RankListFragment());
        fragments.add(new MineFragment());
    }

    private FragmentMainBinding binding;

    private List<Fragment> fragments;
    private static final String TAG = "MainFragment";
    private boolean isJump = true;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (judgeJumpToInfoPerfection()) {
            isJump = false;
            ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_infoPerfectionFragment);
        }
    }

    /**
     * 判断是否跳转到InfoPerfection界面
     *
     * @return true: 要跳转 false:不要跳转
     */
    private boolean judgeJumpToInfoPerfection() {
        return isJump;
    }

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
            WeChatFragment weChatFragment = (WeChatFragment) fragments.get(1);
            weChatFragment.onRefresh();
            return binding.getRoot();
        }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.vpMain.setAdapter(new MyFragmentAdapter(getActivity().getSupportFragmentManager()
                , FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT, fragments));
        binding.vpMain.setOffscreenPageLimit(0);
        binding.vpMain.addOnPageChangeListener(this);
        binding.aa.setOnClickListener(view -> {
            binding.vpMain.setCurrentItem(2, true);
        });
        binding.bnvGuide.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.d(TAG, "onNavigationItemSelected: ");

                switch (item.getItemId()) {
                    case R.id.main:
                        binding.aa.setChecked(false);
                        binding.vpMain.setCurrentItem(0, true);
                        Log.d(TAG, "onNavigationItemSelected: " + 0);
                        break;
                    case R.id.chat:
                        binding.aa.setChecked(false);
                        Log.d(TAG, "onNavigationItemSelected: " + 1);
                        binding.vpMain.setCurrentItem(1, true);
                        break;
                    case R.id.study:
                        Log.d(TAG, "onNavigationItemSelected: " + 2);
//                        binding.vpMain.setCurrentItem(2, true);
                        break;
                    case R.id.selfStudyRoom:
                        binding.aa.setChecked(false);
                        Log.d(TAG, "onNavigationItemSelected: " + 3);
                        binding.vpMain.setCurrentItem(3, true);
                        break;
                    case R.id.mine:
                        binding.aa.setChecked(false);
                        Log.d(TAG, "onNavigationItemSelected: " + 4);
                        binding.vpMain.setCurrentItem(4, true);
                        break;
                }
                return true;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int id = R.id.main;
        switch (position) {
            case 0:
                id = R.id.main;
                break;
            case 1:
                id = R.id.chat;
                break;
            case 2:
                id = R.id.study;
                break;
            case 3:
                id = R.id.selfStudyRoom;
                break;
            case 4:
                id = R.id.mine;
                break;
        }
        binding.bnvGuide.setSelectedItemId(id);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}