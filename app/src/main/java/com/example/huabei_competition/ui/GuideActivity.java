package com.example.huabei_competition.ui;


import android.animation.AnimatorInflater;
import android.animation.StateListAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.huabei_competition.R;
import com.example.huabei_competition.util.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends BaseActivity {


    private static final String TAG = ".GuideActivity";
    private int[] mLayoutIDs = {
            R.layout.guide_one_layout,
            R.layout.guide_two_layout,
            R.layout.guide_three_layout
    };
    private ViewPager viewPager;
    private List<View> mViews;
    private ViewGroup mDotViewGroup;
    private List<ImageView> mDotViews=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        mDotViewGroup=(ViewGroup) findViewById(R.id.dot_layout);
        StateListAnimator animator = AnimatorInflater.loadStateListAnimator(this, R.anim.anim_shake);
        //创建数据
        mViews = new ArrayList<>();
        for (int mLayoutID : mLayoutIDs) {
            final View view = getLayoutInflater().inflate(mLayoutID, null);
            if(mLayoutID == R.layout.guide_three_layout){
                ImageView imageView1 = view.findViewById(R.id.button_guide);
                imageView1.setStateListAnimator(animator);
            }
            mViews.add(view);
            ImageView dot=new ImageView(this);
           //关联dot
            dot.setImageResource(R.drawable.dark_dot);
            dot.setMaxHeight(300);
            dot.setMaxHeight(300);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(20,20);
            layoutParams.leftMargin=20;
            dot.setLayoutParams(layoutParams);
            dot.setEnabled(false);
            mDotViewGroup.addView(dot);
            mDotViews.add(dot);
        }
        viewPager.setAdapter(mPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for (int index = 0; index < mDotViews.size(); index++) {
                    mDotViews.get(index).setImageResource(position==index?R.drawable.white_dot:R.drawable.dark_dot);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    PagerAdapter mPagerAdapter = new PagerAdapter() {
        private Button button;

        @Override
        public int getCount() {
            return mLayoutIDs.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NotNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View child = mViews.get(position);
            container.addView(child);
            return child;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NotNull Object object) {
            container.removeView(mViews.get(position));
        }
    };
    public void StartCheckActivity(View view) {
        Intent intent=new Intent(GuideActivity.this, CheckInActivity.class);
        startActivity(intent);
        finish();
    }

}