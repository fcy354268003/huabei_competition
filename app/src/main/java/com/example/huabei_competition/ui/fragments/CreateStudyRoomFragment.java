package com.example.huabei_competition.ui.fragments;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.CreateGroupCallback;
import com.example.huabei_competition.databinding.FragmentCreateStudyRoomBinding;
import com.example.huabei_competition.event.GroupManager;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;

import java.io.File;
import java.security.Permission;

import cn.jpush.im.android.api.JMessageClient;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateStudyRoomFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateStudyRoomFragment extends Fragment implements CreateGroupCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateStudyRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateStudyRoomFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateStudyRoomFragment newInstance(String param1, String param2) {
        CreateStudyRoomFragment fragment = new CreateStudyRoomFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private FragmentCreateStudyRoomBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup group = (ViewGroup) binding.getRoot().getParent();
            if (group != null) {
                group.removeView(binding.getRoot());
                return binding.getRoot();
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_study_room, container, false);
        binding.setLifecycleOwner(this);
        binding.setCallback(this);
        initFont();
        setObserver();
        return binding.getRoot();
    }

    private void initFont() {
        WidgetUtil.setCustomerText(binding.tvSure, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        WidgetUtil.setCustomerText(binding.tvMoney, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
    }

    @Override
    public void onBackClick() {
        ((MainActivity) getActivity()).getController().navigateUp();
    }

    @Override
    public void onOKClick() {
        // 发送注册申请
        GroupManager.createGroup(binding.etNikeName.getText().toString(),
                binding.etShortDes.getText().toString(), file, WidgetUtil.getImgMimeType(file), new cn.jpush.im.android.api.callback.CreateGroupCallback() {
                    @Override
                    public void gotResult(int i, String s, long l) {
                        if (i == 0) {
                            MyToast.showMessage("创建成功");
                            ((MainActivity) getActivity()).getController().navigateUp();
                        } else MyToast.showMessage("创建失败");
                    }
                });
    }

    public static final int PERMISSION_CODE = 111;

    public static final int REQUEST_CODE = 11;
    private File file;

    @Override
    public void onAvatarClick() {
        // TODO 1.检查权限 2.跳转到图库
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        (getActivity()).startActivityForResult(intent, REQUEST_CODE);
    }

    private void setObserver() {
        LiveDataManager.getInstance().<Uri>with(this.getClass().getSimpleName()).observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                Log.d(TAG, "onChanged: " + uri);
                // TODO 加载图片到UI界面 构造file对象
                binding.imageView2.setImageURI(uri);
                file = WidgetUtil.picToFile(getActivity(), uri);
                if (file != null) {

                } else MyToast.showMessage("获取图片出错");
                // 注册群聊
            }
        });
    }

    private static final String TAG = "CreateStudyRoomFragment";
}