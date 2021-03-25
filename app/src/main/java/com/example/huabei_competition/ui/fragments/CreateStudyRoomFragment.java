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
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.NPCRel;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.Permission;

import cn.jpush.im.android.api.JMessageClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;


public class CreateStudyRoomFragment extends Fragment implements CreateGroupCallback {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // TODO 先判断 是否有100铜币，然后进行再像后端发请求
        NPCRel.createChatRoom(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToast.showMessage("创建失败");
                        }
                    });
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String string = response.body().string();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        if (jsonObject.getString("code").equals(LogIn.OK)) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
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
                                });


                            }
                        } else {
                            onFailure(call, new IOException());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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