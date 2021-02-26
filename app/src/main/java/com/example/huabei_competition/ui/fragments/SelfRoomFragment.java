package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentSelfRoomBinding;
import com.example.huabei_competition.db.Label;
import com.example.huabei_competition.event.ChatRoomUtil;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.DatabaseUtil;
import com.example.huabei_competition.widget.ArrayWheelAdapter;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import cn.jpush.im.android.api.JMessageClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang at 2021/2/12
 */
public class SelfRoomFragment extends Fragment {

    public static final int LIMIT = 1;
    private FragmentSelfRoomBinding binding;
    public static final String ADD_LABEL = "addLabel";


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_self_room, container, false);
        binding.ivAddLabel.setOnClickListener(this::onAddLabelClick);
        binding.btnGo.setOnClickListener(this::startMission);
        binding.rgType.setOnCheckedChangeListener(this::onTypeChange);
        initWheel();
        return binding.getRoot();
    }

    private void onTypeChange(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_person:
                binding.tvPrompt.setText("标签");
                initLabel(0);
                break;
            case R.id.rb_group:
                initLabel(1);
                binding.tvPrompt.setText("自习室");
                break;
        }
    }


    private void initWheel() {
        binding.wvHour.setCyclic(false);
        binding.wvMinute.setCyclic(false);
        List<String> hourList = new ArrayList<>();
        hourList.add("0");
        hourList.add("1");
        hourList.add("2");
        List<String> minuteList = new ArrayList<>();
        for (int i = 0; i < 59; i++) {
            minuteList.add(String.valueOf(i));
        }
        binding.wvHour.setAdapter(new ArrayWheelAdapter<>(hourList));
        binding.wvMinute.setAdapter(new ArrayWheelAdapter<>(minuteList));
    }


    private void initLabel(int type) {
        binding.rgLabel.removeAllViews();
        List<Label> labels;
        if (type == 0)
            labels = DatabaseUtil.getPersonLabels();
        else
            labels = DatabaseUtil.getGroupLabels();
        for (Label label : labels) {
            showLabel(label);
        }
    }

    private void onAddLabelClick(View v) {
        if (binding.rgType.getCheckedRadioButtonId() == R.id.rb_group) {
            // 跳转到自习室界面
            ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_groupSelectedFragment);
        } else {
            CustomerDialog customerDialog = new CustomerDialog();
            customerDialog.setLayoutId(R.layout.dialog_add_label);
            customerDialog.setmWidth(500);
            customerDialog.setCallback(new CustomerDialog.InitCallback() {
                @Override
                public void initWidget(View rootView) {
                    rootView.findViewById(R.id.iv_back).setOnClickListener(v1 -> {
                        customerDialog.dismiss();
                    });
                    rootView.findViewById(R.id.btn_sure).setOnClickListener(v2 -> {
                        String labelName = ((EditText) rootView.findViewById(R.id.et_labelName)).getText().toString();
                        if (TextUtils.isEmpty(labelName))
                            return;
                        Label label = new Label(labelName);
                        DatabaseUtil.save(label);
                        showLabel(label);
                        customerDialog.dismiss();
                    });
                }
            });
            customerDialog.show(getParentFragmentManager(), ADD_LABEL);
        }
    }

    private void showLabel(Label label) {
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 15;
        RadioButton radioButton = new RadioButton(getContext());
        radioButton.setText(label.getLabelName());
        radioButton.setBackground(getResources().getDrawable(R.drawable.radioitem));
        radioButton.setButtonDrawable(R.drawable.back_talk);
        radioButton.setTextSize(18);
        binding.rgLabel.addView(radioButton, binding.rgLabel.getChildCount(), layoutParams);
        long groupId = label.getGroupId();
        if (groupId != 0) {
            radioButton.setTag(groupId);
        }
        radioButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                label.delete();
                return false;
            }
        });
    }

    /**
     * 点击go以后的点击事件
     */
    private void startMission(View v1) {
        // TODO 检查 选择是否规范以及 完整
        View root = binding.getRoot();
        try {
            int typeId = binding.rgType.getCheckedRadioButtonId();
            RadioButton radioType = root.findViewById(typeId);
            String typeText = radioType.getText().toString();
            RadioButton radioLabel = root.findViewById(binding.rgLabel.getCheckedRadioButtonId());
            String labelText = radioLabel.getText().toString();
            int hour = binding.wvHour.getCurrentItem();
            int minute = binding.wvMinute.getCurrentItem();
            int sum = hour * 60 + minute;
            if (sum < LIMIT) {
                MyToast.showMessage("时间不能低于20分钟哦，(●'◡'●)");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("time", sum);
            bundle.putString("label", labelText);
            if (typeText.equals("单人")) {
                bundle.putInt("type", 0);
                ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_selfStudyFragment, bundle);
            } else {
                long groupId = (long) radioLabel.getTag();
                ChatRoomUtil.createChatRoom(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                MyToast.showMessage("网络出错！！");
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NotNull Call call, final @NotNull Response response) throws IOException {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (response.isSuccessful()) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        Long chatRoomId = jsonObject.getLong("chatroom_id");
                                        bundle.putLong("chatRoomId", chatRoomId);
                                        // 跳转到 group等待界面
                                        bundle.putString("groupId", String.valueOf(groupId));
                                        bundle.putInt("type", 1);
                                        ((MainActivity) getActivity())
                                                .getController()
                                                .navigate(R.id.action_mainFragment_to_groupStudyFragment, bundle);
                                    } catch (JSONException | IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    MyToast.showMessage("请求出现异常！！");
                                }
                            }
                        });
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            MyToast.showMessage("请将任务详情填写完整，(●'◡'●)");
        }

    }

    private static final String TAG = "SelfRoomFragment";

}