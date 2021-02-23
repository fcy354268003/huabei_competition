package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.TimerVM;
import com.example.huabei_competition.databinding.FragmentSelfStudyBinding;
import com.example.huabei_competition.ui.activity.MainActivity;

import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.MyCountDownTimer;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTime = getArguments().getInt(TIME, 20);
            mLabelText = getArguments().getString(LABEL);
            timerVM = new TimerVM(mTime);
        }
        Log.d(TAG, "onCreate: " + mTime);
    }

    private static final String TAG = "SelfStudyFragment";


    private FragmentSelfStudyBinding binding;

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
        binding.setLifecycleOwner(this);
        mMotto = binding.tvMotto;
        mLabel = binding.tvLabel;
        String quote = ((MyApplication) getActivity().getApplication()).loadOneQuote();
        mMotto.setText(quote);
        mLabel.setText(mLabelText);
        WidgetUtil.setCustomerText(mMotto, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        myCountDownTimer = new MyCountDownTimer(getContext(), mTime * 60 * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
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
        return binding.getRoot();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (myCountDownTimer != null) {
            myCountDownTimer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (myCountDownTimer.isPause()) {
            myCountDownTimer.restart();
        }
    }
}