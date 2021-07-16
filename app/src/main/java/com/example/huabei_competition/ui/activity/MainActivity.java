package com.example.huabei_competition.ui.activity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.huabei_competition.R;
import com.example.huabei_competition.UserCardVM;
import com.example.huabei_competition.event.LiveDataManager;
import com.example.huabei_competition.ui.fragments.CreateStudyRoomFragment;
import com.example.huabei_competition.ui.mine.MineFragment;
import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;

import java.io.File;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang
 */
public class MainActivity extends BaseActivity {

    private NavController controller;
    private UserCardVM userCardVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        controller = Navigation.findNavController(this, R.id.fragment3);
        userCardVM = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(UserCardVM.class);
    }

    public NavController getController() {
        return controller;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null || data.getData() == null)
            return;
        Uri uri = data.getData();
        if (requestCode == CreateStudyRoomFragment.REQUEST_CODE) {
            LiveDataManager.getInstance().with(CreateStudyRoomFragment.class.getSimpleName()).postValue(uri);
        }
        if (requestCode == MineFragment.REQUEST_CODE) {
            File file = WidgetUtil.picToFile(this, uri);
            if (file != null) {
                JMessageClient.updateUserAvatar(file, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            MyToast.showMessage("头像更新成功");
                            LiveDataManager.getInstance().with(MineFragment.class.getSimpleName()).setValue(new Object());
                        } else MyToast.showMessage("更新失败");
                    }
                });
            }
        }
    }

    public UserCardVM getUserCardVM() {
        return userCardVM;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private static final String TAG = "MainActivity";
}