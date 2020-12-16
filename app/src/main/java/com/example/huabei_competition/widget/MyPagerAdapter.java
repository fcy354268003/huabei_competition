package com.example.huabei_competition.widget;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;


import java.util.ArrayList;
import java.util.List;
/**
 *      Create by GuoZhiYan
 *
 */
public abstract class MyPagerAdapter<T> extends PagerAdapter {
    protected List<T> resources;
    protected List<View> views = new ArrayList<>();
    public MyPagerAdapter(List<T> data) {
        resources = data;
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    protected abstract int getLayoutId(int pos);

    protected abstract void bindView(View view,int pos);

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(getLayoutId(position), container, false);
        bindView(view,position);
        views.add(view);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    public List<View> getViews(){
        return views;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();

    }
}
