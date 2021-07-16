package com.example.huabei_competition.ui.fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;


import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;

import android.view.View;

import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.huabei_competition.R;
import com.example.huabei_competition.TimerVM;
import com.example.huabei_competition.base.BaseFragment;
import com.example.huabei_competition.databinding.FragmentRandomMatchingBinding;
import com.example.huabei_competition.network.copper.EncryptionTransmission;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.StudyDataGet;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.MyCountDownTimer;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RandomMatchingFragment extends BaseFragment<FragmentRandomMatchingBinding> {
    private TextView[] targets;
    private FragmentRandomMatchingBinding binding;
    private MyCountDownTimer myCountDownTimer;
    private TimerVM timerVM;
    private PowerManager powerManager;
    private MediaPlayer mediaPlayer;
    private boolean isMusicOn = false;
    private boolean screenOn = false;
    private ValueAnimator valueAnimator;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTimer();

        targets = new TextView[]{binding.tv11, binding.tv12, binding.tv13, binding.tv14};
        startLoading();
        JMessageClient.getMyInfo().getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    binding.ivMine.setImageBitmap(bitmap);
                }
            }
        });
    }

    private void startLoading() {
        valueAnimator = ObjectAnimator.ofFloat(targets[0], "translationY", 0, 15f);
        valueAnimator.setInterpolator(new AnticipateOvershootInterpolator());
        valueAnimator.setDuration(250);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            int k = 0;

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                k = (k + 1) % 4;
                valueAnimator.setTarget(targets[k]);
                valueAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    private void stopLoading() {
        valueAnimator.pause();
    }

    @Override
    protected void setListener() {
        binding = getBinding();
        binding.include.findViewById(R.id.iv_sun).setOnClickListener(this::onSunClick);
        binding.include.findViewById(R.id.iv_music).setOnClickListener(this::onMusicClick);
        binding.include.findViewById(R.id.iv_breakOff).setOnClickListener(this::onBreakOffClick);
        binding.imageView4.setOnClickListener(this::onBreakOffClick);
    }

    @Override
    protected int setLayoutID() {
        return R.layout.fragment_random_matching;
    }


    @Override
    public void onStop() {
        super.onStop();
        if (powerManager == null)
            powerManager = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
        if (myCountDownTimer != null && powerManager.isInteractive()) {
            myCountDownTimer.pause();
        }
        if (mediaPlayer != null && isMusicOn) {
            mediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myCountDownTimer != null && myCountDownTimer.isPause()) {
            Log.d(TAG, "onResume: myCountDownTimer restart");
            myCountDownTimer.restart();
        }
        if (mediaPlayer != null && isMusicOn) {
            mediaPlayer.start();
        }
    }

    private void onMusicClick(View view) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getContext(), R.raw.background_music);
        }
        isMusicOn = !isMusicOn;
        if (isMusicOn) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }

    private void onBreakOffClick(View view) {
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.customer_dialog);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                TextView title = rootView.findViewById(R.id.tv_title);
                title.setText("真的要退出吗");
                TextView content = rootView.findViewById(R.id.tv_content);
                content.setText("再坚持一会就有奖励了呀");
                rootView.findViewById(R.id.btn_got).setVisibility(View.GONE);
                rootView.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
                    customerDialog.dismiss();
                });
                rootView.findViewById(R.id.btn_confirm).setOnClickListener(view -> {
                    customerDialog.dismiss();
                    ((MainActivity) getActivity()).getController().navigateUp();
                });
            }
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "cancel");
    }


    private void onSunClick(View view) {
        view.setKeepScreenOn(!screenOn);
        screenOn = !screenOn;
    }

    private void initTimer() {
        timerVM = new TimerVM(25);
        myCountDownTimer = new MyCountDownTimer(getContext(), 25 * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(TAG, "onTick: ");
                if (timerVM.getSecond().getValue() > 0) {
                    Long value = timerVM.getSecond().getValue();
                    timerVM.getSecond().setValue(--value);
                } else if (timerVM.getMinute().getValue() > 0) {
                    Long value = timerVM.getMinute().getValue();
                    timerVM.getMinute().setValue(--value);
                    timerVM.getSecond().setValue(59L);
                }
            }

            @Override
            public void onFinish() {
                MyToast.showMessage("完成计时");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StudyDataGet.SubmitTime submitTime = new StudyDataGet.SubmitTime(String.valueOf(25), EncryptionTransmission.encode(LogIn.TOKEN + 25), "随机匹配学习");
                        StudyDataGet.submitTimePerson(submitTime, new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "onResponse: " + response.body().string());
                                }
                                response.close();
                            }
                        });
                    }
                }).start();
                CustomerDialog dialog = new CustomerDialog();
                dialog.setLayoutId(R.layout.dialog_add_label);
                dialog.setCancelable(false);
                dialog.setCallback(new CustomerDialog.InitCallback() {
                    @Override
                    public void initWidget(View rootView) {
                        rootView.findViewById(R.id.iv_back).setVisibility(View.GONE);
                        rootView.findViewById(R.id.et_labelName).setVisibility(View.GONE);
                        TextView title = rootView.findViewById(R.id.tv_title);
                        title.setText("学习及时完成！");
                        TextView money = rootView.findViewById(R.id.tv_moneyReward);
                        money.setVisibility(View.VISIBLE);
                        money.setText("铜钱+" + 50);
                        Button confirm = rootView.findViewById(R.id.btn_sure);
                        confirm.setText("欢喜收下！！");
                        confirm.setOnClickListener(view -> {
                            dialog.dismiss();
                            ((MainActivity) getActivity()).getController().navigateUp();

                        });
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "completeMission");
            }
        };
        binding.setVm(timerVM);
    }

    /**
     * 匹配到以后直接调用startMission
     * 开始倒计时
     */
    private void startMission() {
        binding.llAaa.setVisibility(View.GONE);
        myCountDownTimer.start();
        stopLoading();
    }

    private static final String TAG = "RandomMatchingFragment";
}