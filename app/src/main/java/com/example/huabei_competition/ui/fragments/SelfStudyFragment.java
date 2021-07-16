package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.TimerVM;
import com.example.huabei_competition.databinding.FragmentSelfStudyBinding;
import com.example.huabei_competition.network.copper.EncryptionTransmission;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.StudyDataGet;
import com.example.huabei_competition.ui.activity.MainActivity;

import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.MyCountDownTimer;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Create by FanChenYang at 2021/2/14
 * <p>
 * 单人自习界面
 * </P>
 */
public class SelfStudyFragment extends Fragment {

    public static final String TIME = "time";
    public static final String LABEL = "label";
    private TextView mMotto;
    private int mTime;
    private String mLabelText;
    private TextView mLabel;
    private MyCountDownTimer myCountDownTimer;
    private TimerVM timerVM;
    private static final String TAG = "SelfStudyFragment";
    private FragmentSelfStudyBinding binding;
    private PowerManager powerManager;
    private MediaPlayer mediaPlayer;
    private boolean isMusicOn = false;
    private boolean screenOn = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTime = getArguments().getInt(TIME, 20);
            mLabelText = getArguments().getString(LABEL);
            timerVM = new TimerVM(mTime);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding != null) {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
            ViewGroup mViewGroup = (ViewGroup) binding.getRoot().getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_self_study, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        mMotto = binding.tvMotto;
        mLabel = binding.tvLabel;
        String quote = ((MyApplication) getActivity().getApplication()).loadOneQuote();
        mMotto.setText(quote);
        mLabel.setText(mLabelText);
        WidgetUtil.setCustomerText(mMotto, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        binding.menu.findViewById(R.id.iv_sun).setOnClickListener(this::onSunClick);
        binding.menu.findViewById(R.id.iv_music).setOnClickListener(this::onMusicClick);
        binding.menu.findViewById(R.id.iv_breakOff).setOnClickListener(this::onBreakOffClick);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCountDownTimer();
    }

    private void setCountDownTimer() {
        myCountDownTimer = new MyCountDownTimer(getContext(), mTime * 60 * 1000, 1000) {
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
                        StudyDataGet.SubmitTime submitTime = new StudyDataGet.SubmitTime(String.valueOf(mTime), EncryptionTransmission.encode(LogIn.TOKEN + mTime), mLabelText);
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
                        money.setText("铜钱+" + mTime);
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
        myCountDownTimer.start();
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



    private void onSunClick(View view) {
        view.setKeepScreenOn(!screenOn);
        screenOn = !screenOn;
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
}