package com.example.huabei_competition.ui.activity;

import android.os.Bundle;

import android.view.View;

import androidx.databinding.DataBindingUtil;

import androidx.lifecycle.ViewModelProvider;

import com.example.huabei_competition.R;
import com.example.huabei_competition.RegisterVM;
import com.example.huabei_competition.databinding.ActivityRegist1Binding;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.util.UserUtil;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang
 */
public class RegisterActivity extends BaseActivity implements Callback {
    private ActivityRegist1Binding mBinding;
    private static final String URL = MyApplication.URL + "/app/regist";
    private final MyHandler myHandler = MyHandler.obtain(this);
    private RegisterVM mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(MyApplication.getApplicationByReflect()).create(RegisterVM.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regist_1);
        mBinding.setRegisterVM(mViewModel);
        mBinding.setMContext(this);
    }

    public void onCancel() {
        MyToast.showMessage("取消注册");
        finish();
    }

    public void onConfirm() {
        if (!UserUtil.register(mViewModel.getList(), this)) {
            return;
        }
        mBinding.userName.setVisibility(View.GONE);
        mBinding.password.setVisibility(View.GONE);
        startLoading();
        mBinding.btnCancel.setVisibility(View.GONE);
        mBinding.btnConfirm.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // 强制用户通过指示按钮返回
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        e.printStackTrace();
        finish();
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                MyToast.showMessage("注册失败");
                stopLoading();
            }
        });
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.isSuccessful())
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    MyToast.showMessage("注册成功");
                    finish();
                }
            });
    }

    private static final String TAG = "RegisterActivity";

    /**
     * 发送验证码
     *
     * @param view 发送验证码按钮
     */
    public void sendVerification(View view) {

    }
}