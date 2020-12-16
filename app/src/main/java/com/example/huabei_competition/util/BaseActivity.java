package com.example.huabei_competition.util;

import android.content.res.Configuration;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huabei_competition.R;

import scut.carson_ho.kawaii_loadingview.Kawaii_LoadingView;

/**
 * Create by FanChenYang
 * <p>
 * 控制界面是否全屏显示
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        initFontScale();
    }

    private void initFontScale() {
        Configuration configuration = getResources().getConfiguration();
        configuration.fontScale = (float) 0.85;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getResources().updateConfiguration(configuration, metrics);
    }


    private View loadingView = null;
    private Kawaii_LoadingView loading = null;

    /**
     * 开始播放 等候 动画
     */
    protected void startLoading() {
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

}
