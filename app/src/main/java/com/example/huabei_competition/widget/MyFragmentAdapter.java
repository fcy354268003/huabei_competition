package com.example.huabei_competition.widget;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyFragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments;
    private final List<String> titleList;

    public MyFragmentAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> res) {
        super(fm, behavior);
        fragments = res;
        titleList = new ArrayList<>();
    }

    public MyFragmentAdapter(@NonNull FragmentManager fm, int behavior, List<Fragment> res, List<String> titles) {
        super(fm, behavior);
        fragments = res;
        titleList = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: " + position);
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = titleList.get(position);
        if (title != null)
            return title;

        return super.getPageTitle(position);
    }

    private static final String TAG = "MyFragmentAdapter";
}
