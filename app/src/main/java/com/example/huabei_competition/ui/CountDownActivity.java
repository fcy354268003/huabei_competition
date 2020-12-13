package com.example.huabei_competition.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huabei_competition.R;
import com.example.huabei_competition.fcyUtil.MyApplication;
import com.example.huabei_competition.fcyUtil.MyReceiver;
import com.example.huabei_competition.fcyUtil.MyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author fcy
 */
public class CountDownActivity extends AppCompatActivity {
    private CountDownTimer countDownTimer;
    private TextView mHour, mMinute;
    private int hour, minute;
    private BroadcastReceiver mBroadcastReceiver;
    private boolean isStudy;  // 是否已经开始学习
    private int userLeave;
    private int final_hour, final_minute;
    private String URL = MyApplication.URL + "/app/updateTime";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_down1);
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            Uri uri = Uri.parse("package:" + getPackageName());
            intent.setData(uri);
            startActivityForResult(intent, 11);
        }
        mBroadcastReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("aaa");
        registerReceiver(mBroadcastReceiver, intentFilter);
        String[] hours = getResources().getStringArray(R.array.hour);
        final String[] minutes = getResources().getStringArray(R.array.minute);
        ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, hours);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, minutes);
        final Spinner sp_hour = findViewById(R.id.sp_hour), sp_minute = findViewById(R.id.sp_minute);
        sp_hour.setAdapter(arrayAdapter1);
        sp_minute.setAdapter(arrayAdapter2);
        ImageView imageView = findViewById(R.id.iv_start);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: " + sp_hour.getSelectedItemPosition() + sp_minute.getSelectedItemPosition());
                hour = sp_hour.getSelectedItemPosition();
                minute = sp_minute.getSelectedItemPosition() * 5;
                if (hour == 0 && minute == 0) {
                    Toast.makeText(CountDownActivity.this, "请正确设置学习时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                isStudy = true;
                setContentView(R.layout.activity_count_down);
                mHour = findViewById(R.id.tv_hour);
                mMinute = findViewById(R.id.tv_minute);
                final_hour = hour;
                final_minute = minute;
                mHour.setText(hour + " 时 ");
                mMinute.setText(minute + " 分 ");
                findViewById(R.id.wrapper3).setVisibility(View.VISIBLE);
                findViewById(R.id.wrapper1).setVisibility(View.GONE);
                findViewById(R.id.wrapper2).setVisibility(View.GONE);
                View view2 = findViewById(R.id.wrapper2);
                view2.setVisibility(View.GONE);
                final LinearLayout linearLayout = findViewById(R.id.ll_all);
                linearLayout.setBackgroundResource(R.drawable.study_background);
                minute = 1;
                countDownTimer = new MyCountDownTimer((hour * 60 + minute) * 60 * 1000, 1000 * 60);
                countDownTimer.start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isStudy && userLeave == 1) {
            userLeave = 0;
            MyToast.showMessage("继续学习", CountDownActivity.this);
            countDownTimer = new MyCountDownTimer((hour * 60 + minute) * 60 * 1000, 1000 * 60);
            countDownTimer.start();
            mMinute.setText(minute + "分");
            mHour.setText(hour + "时");

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStop() {
        super.onStop();
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (powerManager.isInteractive() && Settings.canDrawOverlays(this) && isStudy) {
            Intent intent = new Intent("aaa");
            sendBroadcast(intent);
            if (countDownTimer != null)
                countDownTimer.cancel();
            countDownTimer = null;
            userLeave = 1;
        }
    }

    // 让用户无法左滑返回
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK && isStudy)
            return true;
        else return super.onKeyDown(keyCode, event);
    }


    private static final String TAG = "CountDownActivity";

    //返回键跳回主页
    public void StartMainActivity(View view) {
        finish();
    }

    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        private boolean isFirst = true;

        @Override
        public void onTick(long millisUntilFinished) {
            Log.d(TAG, "onTick: " + "minute:" + minute + " hour: " + hour);

            if (isFirst) {
                isFirst = false;
                return;
            }

            if (minute > 0) {
                minute--;
                String string1 = minute + " 分 ";
                mMinute.setText(string1);
            } else if (hour > 0) {
                hour--;
                minute = 59;
                String string2 = hour + " 时 ";
                mHour.setText(string2);
                String string3 = minute + " 分 ";
                mMinute.setText(string3);
            }
        }

        @Override
        public void onFinish() {
            final View view3 = findViewById(R.id.wrapper3);
            final View view1 = findViewById(R.id.wrapper1);
            TextView textView = findViewById(R.id.tv_ok);
            textView.setVisibility(View.VISIBLE);
            view3.setVisibility(View.GONE);
            view1.setVisibility(View.VISIBLE);
            ImageView imageView1 = findViewById(R.id.iv_start);
            imageView1.setImageResource(R.drawable.count_down_start);
            imageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 跳转到奖励界面
                    Intent intent = new Intent(CountDownActivity.this, StoryChoiceActivity.class);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                    finish();
                }
            });
//            final LinearLayout linearLayout = findViewById(R.id.ll_all);
//            linearLayout.setBackgroundResource(R.drawable.);
            isStudy = false;
//            OkHttpClient client = new OkHttpClient();
            OkHttpClient client = ((MyApplication) getApplication()).okHttpClient;
            FormBody formBody = new FormBody.Builder().add("time", final_hour * 60 + final_minute + "").build();
            Request request = new Request.Builder().put(formBody).url(URL).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showMessage("数据上传失败", CountDownActivity.this);
                        }
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        MyApplication application = (MyApplication) getApplication();
                        application.getUser().setStudyTime(application.getUser().getStudyTime() + final_hour * 60 + final_minute);
                    }
                }
            });
        }
    }

}