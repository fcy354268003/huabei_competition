package com.example.huabei_competition.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;

import androidx.annotation.NonNull;

import com.example.huabei_competition.R;
import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.ui.login.CheckInActivity;

import java.lang.ref.WeakReference;

public class StartActivity extends BaseActivity {
    private SharedPreferences mSharedPreference;
    private static final int GO_CHECK = 1;
    private static final int GO_GUIDE = 2;
    private static final int ENTEER_DURATION = 3000;
    private Handler mHandler = new Handler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mSharedPreference = getSharedPreferences("config", MODE_PRIVATE);
        init();
    }

    //是否第一次
    private void init() {
        boolean isFirstIn = mSharedPreference.getBoolean("mIsFirstIn", true);
        if (isFirstIn) {
            SharedPreferences.Editor edit = mSharedPreference.edit();
            edit.putBoolean("mIsFirstIn", false);
            edit.apply();
            mHandler.sendEmptyMessageDelayed(GO_GUIDE, ENTEER_DURATION);
        } else {
            mHandler.sendEmptyMessageDelayed(GO_CHECK, ENTEER_DURATION);

        }
    }

    //进入登录页
    private void StartCheckActivity() {
        Intent intent = new Intent(StartActivity.this, CheckInActivity.class);
        startActivity(intent);
        finish();
    }

    //进入引导页
    private void StartGuideActivity() {
        Intent intent = new Intent(StartActivity.this, GuideActivity.class);
        startActivity(intent);
        finish();
    }

    private static class Handler extends android.os.Handler {
        WeakReference<StartActivity> weakReference = null;

        private Handler(StartActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (weakReference == null || weakReference.get() == null)
                return;
            StartActivity activity = weakReference.get();

            switch (msg.what) {
                //首次启动跳到引导页
                case GO_GUIDE:
                    activity.StartGuideActivity();
                    break;
                //跳到登录页
                case GO_CHECK:
                    activity.StartCheckActivity();
                    break;
                default:
                    break;
            }
        }
    }


}