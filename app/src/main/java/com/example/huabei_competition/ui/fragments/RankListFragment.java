package com.example.huabei_competition.ui.fragments;


import android.graphics.Paint;

import android.os.Bundle;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.RankListCallback;
import com.example.huabei_competition.databinding.FragmentRankListBinding;
import com.example.huabei_competition.ui.activity.MainActivity;

/**
 *      Create by FanChenYang at 2021/2/8
 *
 */
public class RankListFragment extends Fragment implements RankListCallback {


    private FragmentRankListBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rank_list, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallback(this);
        initSelectedIndicator();
        return binding.getRoot();
    }

    private int selected = 1;

    /**
     * 初始化列表的adapter，同时加载两条内容，缓存第二条内容
     */
    private void initAdapter() {

    }


    private void initSelectedIndicator() {
        binding.tvNewRank.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.tvNewRank.setTextColor(getResources().getColor(R.color.tabSelected_group));
        binding.tvNewRank.setOnClickListener(new_ -> {
            if (selected == 1)
                return;
            selected = 1;
            // TODO 切换列表adapter
            binding.tvNewRank.setTextSize(40);
            binding.tvRank.setTextSize(18);
            binding.tvNewRank.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            binding.tvNewRank.setTextColor(getResources().getColor(R.color.tabSelected_group));
            binding.tvRank.setTextColor(getResources().getColor(R.color.tabUnSelected_group));
            binding.tvRank.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
        });
        binding.tvRank.setOnClickListener(rank -> {
            if (selected == 2)
                return;
            selected = 2;
            // TODO 切换列表adapter
            binding.tvNewRank.setTextSize(18);
            binding.tvRank.setTextSize(40);
            binding.tvRank.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
            binding.tvNewRank.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            binding.tvRank.setTextColor(getResources().getColor(R.color.tabSelected_group));
            binding.tvNewRank.setTextColor(getResources().getColor(R.color.tabUnSelected_group));
        });
    }

    private static final String TAG = "RankListFragment";

    @Override
    public void onSearchClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_searchFragment);
    }

    @Override
    public void onAddClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_createStudyRoomFragment);
    }
}