package com.example.huabei_competition.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.bumptech.glide.Glide;
import com.example.huabei_competition.R;
import com.example.huabei_competition.UserCardVM;
import com.example.huabei_competition.callback.MineCallback;
import com.example.huabei_competition.databinding.FragmentMineBinding;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.ui.activity.CheckInActivity;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyToast;

import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;

/**
 * Create by FanChenYang at 2021/2/7
 */
public class MineFragment extends Fragment implements MineCallback {
    public static final int REQUEST_CODE = 2000;
    public static final int PERMISSION_CODE = 20001;

    private FragmentMineBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
                return binding.getRoot();
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mine, container, false);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private static final String TAG = "MineFragment";

    private void initData() {
        UserCardVM userCardVM = ((MainActivity) getActivity()).getUserCardVM();
        binding.setViewModel(userCardVM);
        if (userCardVM.getMyInfo() != null) {
            userCardVM.getMyInfo().getAvatarBitmap(new GetAvatarBitmapCallback() {
                @Override
                public void gotResult(int i, String s, Bitmap bitmap) {
                    Log.d(TAG, "gotResult: " + s);
                    if (i == 0) {
                        binding.ivPortrait.setImageBitmap(bitmap);
                    }
                }
            });
        }
    }

    /**
     * 给各个控件设置点击事件
     */
    private void setListener() {
        binding.ivPortrait.setOnLongClickListener(this::onPortraitLongPress);
        binding.tvNickName.setOnLongClickListener(this::onNickNameLongPress);
        binding.btnModifyAccount.setOnClickListener(this::onModifyAccountPress);
        binding.ivSecurity.setOnClickListener(this::onSecuritySettingPress);
    }


    @Override
    public boolean onPortraitLongPress(View avatar) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            return true;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        (getActivity()).startActivityForResult(intent, REQUEST_CODE);
        return true;
    }

    @Override
    public boolean onNickNameLongPress(View a) {
        // TODO 更改昵称
        return true;
    }

    @Override
    public void onSecuritySettingPress(View a) {
        //TODO 更改密码
    }

    @Override
    public void onModifyAccountPress(View a) {
        Intent intent = new Intent(getContext(), CheckInActivity.class);
        startActivity(intent);
        // TODO 切换用户
        UserUtil.logOut((MainActivity) getActivity());
        getActivity().finish();
    }
}