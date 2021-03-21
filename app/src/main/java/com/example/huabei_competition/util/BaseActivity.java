package com.example.huabei_competition.util;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huabei_competition.R;
import com.example.huabei_competition.ui.activity.CheckInActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import scut.carson_ho.kawaii_loadingview.Kawaii_LoadingView;

/**
 * Create by FanChenYang
 * <p>
 * 控制界面是否全屏显示
 */
public abstract class BaseActivity extends AppCompatActivity {
    private final List<Activity> activities = new ArrayList<>();

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            View decorView = getWindow().getDecorView();
//            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//            );
//        }
//        getWindow().setStatusBarColor(Color.TRANSPARENT);
//        getWindow().setNavigationBarColor(Color.TRANSPARENT);
//
//    }

    private void initFontScale() {
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 0.85;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getResources().updateConfiguration(configuration, metrics);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activities.add(this);
        initFontScale();
    }

    @Override
    protected void onDestroy() {
        activities.remove(this);
        MyHandler.removeByKey(this);
        super.onDestroy();
    }

    public void logOut() {
        for (Activity activity : activities) {
            activity.finish();
        }
        startActivity(new Intent(this, CheckInActivity.class));
    }

    private View loadingView = null;
    private Kawaii_LoadingView loading = null;

    /**
     * 开始播放 等候 动画
     */
    public void startLoading() {
        if (loadingView == null)
            loadingView = findViewById(R.id.loading);
        if (loadingView == null)
            throw new Error("未找到加载view");
        loadingView.setVisibility(View.VISIBLE);
        if (loading == null)
            loading = loadingView.findViewById(R.id.kawaii);
        loading.startMoving();
    }

    /**
     * 停止播放 等候 动画
     */
    protected void stopLoading() {
        loading.stopMoving();
        loadingView.setVisibility(View.GONE);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 点击空白 隐藏软键盘

        InputMethodManager methodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (ev.getAction() == KeyEvent.ACTION_DOWN && getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            methodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return super.dispatchTouchEvent(ev);
    }

    private static final String TAG = "BaseActivity";

    @Override
    public void onBackPressed() {

    }
}
