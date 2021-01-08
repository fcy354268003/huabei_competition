package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.example.huabei_competition.R;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.ui.fragments.FamousQuotesFragment;
import com.example.huabei_competition.util.UserUtil;

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
        setContentView(R.layout.activity_main);
//        Log.d(TAG, "onCreate: " + ((MyApplication) getApplication()).getUser().toString());
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        init();
    }

    private void init() {
        ImageView imageView = findViewById(R.id.imageView);
        TextView textView = findViewById(R.id.textView);
        ImageButton imageButton = findViewById(R.id.study_study);
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragments[0]).commit();

        frameLayout = findViewById(R.id.container);
        alpha_views.add(imageView);
        alpha_views.add(textView);
        alpha_views.add(imageButton);

        mAnimationSet = new AnimationSet(true);
        mAnimationSet.setDuration(3000);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
        mAnimationSet.addAnimation(alphaAnimation);


        mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_shake);
        mAnimation.setRepeatMode(Animation.REVERSE);

        // 设置点击事件
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CountDownActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.ib_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StoryChoiceActivity.class);
                intent.putExtra("type", 0);
                startActivity(intent);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowing = frameLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
                frameLayout.setVisibility(isShowing);
            }
        });
        findViewById(R.id.tongji).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setShake() {
        for (View view : views) {
            view.startAnimation(mAnimation);
        }
        for (View alpha_view : alpha_views) {
            alpha_view.startAnimation(mAnimationSet);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setShake();
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