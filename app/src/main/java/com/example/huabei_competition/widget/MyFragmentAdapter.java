package com.example.huabei_competition.widget;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments;

    public MyFragmentAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> res) {
        super(fm, behavior);
        fragments = res;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
