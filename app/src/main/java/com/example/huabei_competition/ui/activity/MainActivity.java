package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.huabei_competition.R;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.ui.fragments.FamousQuotesFragment;
import com.example.huabei_competition.util.UserUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by FanChenYang
 */
public class MainActivity extends BaseActivity {
    private MediaPlayer mMediaPlayer;
    private int current_position = 0;
    private List<View> views = new ArrayList<>();
    private Animation mAnimation;
    private AnimationSet mAnimationSet;
    private List<View> alpha_views = new ArrayList<>();
    private FrameLayout frameLayout;
    private Fragment[] fragments = new Fragment[]{new FamousQuotesFragment()};
    private static final String TAG = "MainActivity";
    private int isShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
//        init();
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mMediaPlayer == null) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.background_music);
            mMediaPlayer.start();
        } else {
            mMediaPlayer.seekTo(current_position);
            mMediaPlayer.start();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            current_position = mMediaPlayer.getCurrentPosition();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void changeUser(View view) {
        UserUtil.logOut(this);
    }
}