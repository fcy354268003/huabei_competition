package com.example.huabei_competition.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.TimerVM;
import com.example.huabei_competition.databinding.FragmentGroupStudyBinding;

import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.StudyDataGet;
import com.example.huabei_competition.network.api.XhhEnc;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.MyCountDownTimer;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyRecyclerAdapter;
import com.example.huabei_competition.widget.MyToast;
import com.kd.easybarrage.Barrage;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.event.CommandNotificationEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/24
 */
public class GroupStudyFragment extends Fragment {
    private FragmentGroupStudyBinding binding;
    private List<UserInfo> userInfo;
    private int mTime;
    private MyRecyclerAdapter<UserInfo> userInfoMyRecyclerAdapter;
    private MyCountDownTimer myCountDownTimer;
    private TimerVM timerVM;
    private PowerManager powerManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding != null) {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
            ViewGroup mViewGroup = (ViewGroup) binding.getRoot().getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_study, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        initArgs();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
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
            myCountDownTimer.restart();
        }
        if (mediaPlayer != null && isMusicOn) {
            mediaPlayer.start();
        }
    }

    /**
     * 初始化参数
     */
    private void initArgs() {
        mTime = getArguments().getInt("time");
        LiveDataManager.getInstance().<List<UserInfo>>with(GroupStudyFragment.class.getSimpleName()).observe(getViewLifecycleOwner(), new Observer<List<UserInfo>>() {
            @Override
            public void onChanged(List<UserInfo> userInfos) {
                Log.d(TAG, "onChanged: " + userInfos.size());
                userInfo = userInfos;
                initRecycler();
            }
        });
        LiveDataManager.getInstance().<CommandNotificationEvent>with(GroupStudyFragment.class.getSimpleName() + "barrage").observe(getViewLifecycleOwner(), new Observer<CommandNotificationEvent>() {
            @Override
            public void onChanged(CommandNotificationEvent event) {
                String msg = event.getMsg();
                //TODO 显示弹幕
                if (msg.startsWith(BARRAGE_PRE)) {
                    String realContent = msg.replace(BARRAGE_PRE, "(ง •_•)ง");
                    // 显示弹幕
                    showBarrage(realContent);
                }
            }
        });
    }

    private void showBarrage(String barrage) {
        binding.bvBarrageCon.addBarrage(new Barrage(barrage));
    }

    private static final String TAG = "GroupStudyFragment";

    private void initData() {
        //TODO 初始化成员列表 初始化倒计时器
        initCountDownTimer();
        binding.btnSendMessage.setOnClickListener(this::sendMessage);
        binding.menu.findViewById(R.id.iv_sun).setOnClickListener(this::onSunClick);
        binding.menu.findViewById(R.id.iv_music).setOnClickListener(this::onMusicClick);
        binding.menu.findViewById(R.id.iv_breakOff).setOnClickListener(this::onBreakOffClick);
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
                    ((MainActivity) getActivity()).getController().navigate(R.id.action_groupStudyFragment_to_mainFragment);
                });
            }
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "cancel");
    }

    private MediaPlayer mediaPlayer;
    private boolean isMusicOn = false;

    private void onMusicClick(View view) {
        //TODO 播放音乐 等操作
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

    private boolean screenOn = false;

    private void onSunClick(View view) {
        view.setKeepScreenOn(!screenOn);
        screenOn = !screenOn;
    }

    private void sendMessage(View view) {
        if (!view.isClickable()) {
            MyToast.showMessage("每三分钟才可以发送一条弹幕哦（＞人＜；）");
        }
        // 向聊天室发送一条弹幕
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.dialog_add_label);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                TextView title = rootView.findViewById(R.id.tv_title);
                title.setText("发送弹幕");
                final EditText barrageContent = rootView.findViewById(R.id.et_labelName);
                rootView.findViewById(R.id.btn_sure).setOnClickListener(view -> {
                    //发送一条消息
                    String content = barrageContent.getText().toString();
                    if (TextUtils.isEmpty(content)) {
                        MyToast.showMessage("发送消息不能为空");
                        return;
                    }
                    customerDialog.dismiss();
                    sendCMD(content);
                    //TODO 冷却发送按钮
                    binding.btnSendMessage.setClickable(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (binding != null) {
                                binding.btnSendMessage.setClickable(true);
                            }
                        }
                    }, 3 * 60 * 1000);
                });
                rootView.findViewById(R.id.iv_back).setOnClickListener(view -> {
                    customerDialog.dismiss();
                });
            }
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "barrageShow");
    }

    public static final String BARRAGE_PRE = "0x000";

    private void sendCMD(String content) {
        showBarrage(content);
        content = BARRAGE_PRE + content;
        Log.d(TAG, "sendCMD: " + userInfo.size());
        for (UserInfo userInfo : userInfo) {
            JMessageClient.sendSingleTransCommand(userInfo.getUserName(), null, content, new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    Log.d(TAG, "gotResult: 发送透传消息" + s);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initRecycler() {
        if (userInfo == null)
            return;
        userInfoMyRecyclerAdapter = new MyRecyclerAdapter<UserInfo>(userInfo) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item_study_recyclerview;
            }

            @Override
            public void bindView(MyHolder holder, int position, UserInfo userInfo) {
                Log.d(TAG, "bindView: ");
                ImageView avatarView = holder.getView(R.id.iv_memberAvatar);
                userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        avatarView.setImageBitmap(bitmap);
                    }
                });
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rvMembers.setLayoutManager(linearLayoutManager);
        binding.rvMembers.setAdapter(userInfoMyRecyclerAdapter);
    }

    private void initCountDownTimer() {
        timerVM = new TimerVM(mTime);
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
                        StudyDataGet.submitTimeTeam(new StudyDataGet.SubmitTime(String.valueOf(mTime), XhhEnc.enc(LogIn.TOKEN + mTime), "小组学习"), new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {

                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                Log.d(TAG, "onResponse: " + response.body().string());
                                if (response.isSuccessful()) {

                                }
                                response.body().close();
                            }
                        });
                    }
                }).start();

                CustomerDialog dialog = new CustomerDialog();
                dialog.setLayoutId(R.layout.dialog_add_label);
                dialog.setCancelable(false);
                binding.bvBarrageCon.destroy();
                dialog.setCallback(new CustomerDialog.InitCallback() {
                    @Override
                    public void initWidget(View rootView) {
                        rootView.findViewById(R.id.iv_back).setVisibility(View.GONE);
                        rootView.findViewById(R.id.et_labelName).setVisibility(View.GONE);
                        TextView title = rootView.findViewById(R.id.tv_title);
                        title.setText("学习及时完成！");
                        TextView money = rootView.findViewById(R.id.tv_moneyReward);
                        money.setVisibility(View.VISIBLE);
                        money.setText("铜钱+" + (mTime * 2));
                        Button confirm = rootView.findViewById(R.id.btn_sure);
                        confirm.setText("欢喜收下！！");
                        confirm.setOnClickListener(view -> {
                            dialog.dismiss();
                            ((MainActivity) getActivity()).getController().navigate(R.id.action_groupStudyFragment_to_mainFragment);
                        });
                    }
                });
                dialog.show(getActivity().getSupportFragmentManager(), "completeMission");
            }
        };
        binding.setVm(timerVM);
        myCountDownTimer.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.bvBarrageCon.destroy();
    }
}
