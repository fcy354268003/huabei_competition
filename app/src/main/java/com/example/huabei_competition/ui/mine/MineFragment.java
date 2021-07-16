package com.example.huabei_competition.ui.mine;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.UserCardVM;
import com.example.huabei_competition.base.BaseFragment;
import com.example.huabei_competition.callback.MineCallback;
import com.example.huabei_competition.databinding.FragmentMineBinding;
import com.example.huabei_competition.db.Product;
import com.example.huabei_competition.db.Prop;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.ui.login.CheckInActivity;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;

import org.litepal.LitePal;

import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/2/7
 */
public class MineFragment extends BaseFragment<FragmentMineBinding> implements MineCallback {
    public static final int REQUEST_CODE = 2000;
    public static final int PERMISSION_CODE = 20001;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        LiveDataManager.getInstance().with(MineFragment.class.getSimpleName()).observe(getViewLifecycleOwner(), new Observer<Object>() {
            @Override
            public void onChanged(Object o) {
                JMessageClient.getMyInfo().getAvatarBitmap(new GetAvatarBitmapCallback() {
                    @Override
                    public void gotResult(int i, String s, Bitmap bitmap) {
                        if (i == 0) {
                            binding.ivPortrait.setImageBitmap(bitmap);
                        }
                    }
                });
            }
        });
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
    @Override
    protected void setListener() {
        binding.ivPortrait.setOnLongClickListener(this::onPortraitLongPress);
        binding.tvNickName.setOnLongClickListener(this::onNickNameLongPress);
        binding.btnModifyAccount.setOnClickListener(this::onModifyAccountPress);
        binding.ivSecurity.setOnClickListener(this::onSecuritySettingPress);
    }

    @Override
    protected int setLayoutID() {
        return R.layout.fragment_mine;
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
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.dialog_add_label);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                TextView title = rootView.findViewById(R.id.tv_title);
                title.setText("修改昵称");
                EditText editText = rootView.findViewById(R.id.et_labelName);
                editText.setHint("昵称：");
                rootView.findViewById(R.id.iv_back).setOnClickListener(view1 -> {
                    customerDialog.dismiss();
                });
                rootView.findViewById(R.id.btn_sure).setOnClickListener(view -> {
                    int length = editText.getText().toString().length();
                    if (TextUtils.isEmpty(editText.getText().toString()) || length < 2 || length > 10) {
                        MyToast.showMessage("昵称不合规范");
                        return;
                    }
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    myInfo.setNickname(editText.getText().toString());
                    JMessageClient.updateMyInfo(UserInfo.Field.nickname, myInfo, new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            customerDialog.dismiss();
                            if (i == 0) {
                                MyToast.showMessage("更新成功");
                            } else {
                                MyToast.showMessage(s);
                            }
                        }
                    });
                });
            }
        });
        customerDialog.setCancelable(false);
        customerDialog.show(getActivity().getSupportFragmentManager(), "");
        return true;
    }

    @Override
    public void onSecuritySettingPress(View a) {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_changePassFragment);
    }

    @Override
    public void onModifyAccountPress(View a) {
        Intent intent = new Intent(getContext(), CheckInActivity.class);
        startActivity(intent);
        UserUtil.logOut((MainActivity) getActivity());
        getActivity().finish();
    }
}