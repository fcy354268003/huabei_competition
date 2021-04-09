package com.example.huabei_competition.ui.fragments;

import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.DataShowCallback;
import com.example.huabei_competition.databinding.FragmentStatisticsBinding;
import com.example.huabei_competition.event.FriendManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.StudyDataGet;
import com.example.huabei_competition.ui.activity.MainActivity;

import com.example.huabei_competition.widget.CakeShapeView;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Create by FanChenYang at 2021/3/1
 */
public class DataShowFragment extends Fragment implements DataShowCallback, Callback {

    private FragmentStatisticsBinding binding;
    private String currentUserName;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LiveDataManager.getInstance().<String>with(DataShowFragment.class.getSimpleName()).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                currentUserName = s;
                if (TextUtils.equals(s, UserUtil.sUserName)) {
                    initData(0);
                } else {
                    initData(1);
                }
            }
        });
    }

    private static final String TAG = "DataShowFragment";

    /**
     * @param type 查看自己信息还是查看别人信息
     *             0：self   1:others
     */
    private void initData(int type) {
        JMessageClient.getUserInfo(currentUserName, new GetUserInfoCallback() {
            @Override
            public void gotResult(int i, String s, UserInfo userInfo) {
                if (i == 0) {
                    String nickname = userInfo.getNickname();
                    String userName = userInfo.getUserName();
                    if (TextUtils.isEmpty(nickname)) {
                        binding.tvUserName.setText(userName);
                    } else {
                        binding.tvUserName.setText(nickname);
                    }
                    userInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
                        @Override
                        public void gotResult(int i, String s, Bitmap bitmap) {
                            if (i == 0) {
                                binding.ivMinePortrait.setImageBitmap(bitmap);
                            }
                        }
                    });
                }
            }
        });
        if (type == 0) {
            binding.cvAddToMyFriend.setVisibility(View.GONE);
        }
        binding.cvAddToMyFriend.setOnClickListener(view -> {
            FriendManager.sendInvitationRequest(currentUserName, null, "可以一块学习吗", new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        MyToast.showMessage("邀请信息已发送");
                    } else {
                        MyToast.showMessage(s);
                    }
                }
            });
        });
        StudyDataGet.getUserData(currentUserName, this);
    }

    private void initView(StudyDataGet.UserData userData) {
        StudyDataGet.UserData.DataDTO data = userData.getData();
        String allNumber = data.getAllNumber();
        String allTime = data.getAllTime();
        String avgTime = data.getAvgTime();
        String time = data.getTime();
        String number = data.getNumber();
        binding.cishuTv.setText(allNumber);
        binding.totalTv.setText(allTime);
        binding.weekTv.setText(avgTime);
        binding.tvTodayFrequency.setText(number);
        binding.tvTodayMin.setText(time);
        List<CakeShapeView.Content> contents = new ArrayList<>();
        List<StudyDataGet.UserData.DataDTO.InfoDTO> info = data.getInfo();
        Log.d(TAG, "initView: " + info.size());
        if (info.size() == 0) {
            initOnNoNetWork();
            return;
        }
        int i = 0;
        for (StudyDataGet.UserData.DataDTO.InfoDTO infoDTO : info) {
            String name = infoDTO.getName();
            String size = infoDTO.getSize();
            contents.add(new CakeShapeView.Content(name + ":" + size + "%", Integer.parseInt(size), colors[i++]));
            i = i % 7;
        }
        Log.d(TAG, "initView: " + "setData");
        binding.cakeView.setData(contents, 180);
    }

    private int[] colors = new int[]{
            0xFFf4dccb,
            0xFFf5cec7,
            0xffffb383,
            0xffffeb70,
            0xffedb5d2,
            0xfffff5e9,
            0xffff7b89,
            0xffc0bccc,
            0xffc8a8da
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            //缓存的rootView需要判断是否已经被加过parent， 如果有parent则从parent删除，防止发生这个rootview已经有parent的错误。
            ViewGroup mViewGroup = (ViewGroup) binding.getRoot().getParent();
            if (mViewGroup != null) {
                mViewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_statistics, container, false);
        binding.setCallback(this);
        initOnNoNetWork();
        return binding.getRoot();
    }

    boolean onNoNetWorkState = false;

    /**
     * 记载数据失败时
     */
    private void initOnNoNetWork() {
        Log.d(TAG, "initOnNoNetWork: ");
        if (getActivity() == null)
            return;
        if (onNoNetWorkState)
            return;
        onNoNetWorkState = !onNoNetWorkState;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: ");
                CakeShapeView.Content ca = new CakeShapeView.Content("暂无数据", 100, colors[0]);
                List<CakeShapeView.Content> contents = new ArrayList<>();
                contents.add(ca);
                Log.d(TAG, "initView: " + "setData");
                binding.cakeView.setData(contents, 180);

            }
        });
    }

    @Override
    public void onBackClick() {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    @Override
    public void onQuestionClick() {
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.fragment_question);
        customerDialog.setCancelable(false);
        customerDialog.setmHeight(1500);
        customerDialog.setCallback(view -> {
            view.findViewById(R.id.iv_close).setOnClickListener(v -> {
                customerDialog.dismiss();
            });
            TextView content = view.findViewById(R.id.tv_help);
            WidgetUtil.setCustomerText(content, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "question");
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        initOnNoNetWork();
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.isSuccessful()) {
            String json = response.body().string();
            Log.d(TAG, "onResponse: " + json);
            Gson gson = new Gson();
            StudyDataGet.UserData userData = gson.fromJson(json, StudyDataGet.UserData.class);
            if (TextUtils.equals(userData.getCode(), LogIn.OK)) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initView(userData);
                    }
                });
            }
        } else initOnNoNetWork();
        response.close();

    }
}
