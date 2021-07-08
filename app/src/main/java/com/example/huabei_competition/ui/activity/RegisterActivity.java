package com.example.huabei_competition.ui.activity;

import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;

import androidx.lifecycle.ViewModelProvider;

import com.example.huabei_competition.R;
import com.example.huabei_competition.RegisterVM;
import com.example.huabei_competition.databinding.ActivityRegist1Binding;
import com.example.huabei_competition.network.api.Register;
import com.example.huabei_competition.base.BaseActivity;
import com.example.huabei_competition.network.base.NetRequestCallback;
import com.example.huabei_competition.util.DataFormat;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.options.RegisterOptionalUserInfo;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;

/**
 * Create by FanChenYang
 */
public class RegisterActivity extends BaseActivity implements Register.VerificationCallback, NetRequestCallback {
    private ActivityRegist1Binding mBinding;
    private final MyHandler myHandler = MyHandler.obtain(this, null);
    private RegisterVM mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(MyApplication.getApplicationByReflect()).create(RegisterVM.class);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_regist_1);
        mBinding.setRegisterVM(mViewModel);
        mBinding.setMContext(this);
        mBinding.setLifecycleOwner(this);
    }

    public void onCancel() {
        MyToast.showMessage("取消注册");
        finish();
    }

    public void onConfirm() {
        // 检查是否符合规范
        if (DataFormat.isStandard(mViewModel.getList())) {
            startAni();
            Register.register(mViewModel.toParams(), this);
        }
    }

    private void startAni() {
        mBinding.first.setVisibility(View.GONE);
        startLoading();
    }


    private static final String TAG = "RegisterActivity";

    /**
     * 发送验证码
     *
     * @param view 发送验证码按钮
     */
    public void sendVerification(View view) {
        String value = mViewModel.getPhoneNumber().getValue();
        if (TextUtils.isEmpty(value) || value.length() != 11) {
            MyToast.showMessage("请将按照规范填写电话号码");
            return;
        }
        if (!view.isClickable()) {
            MyToast.showMessage("一分钟之内只能点击一次哦");
            return;
        }
        view.setClickable(false);
        MyToast.showMessage("消息已发送");
        view.setBackgroundColor(getResources().getColor(R.color.gray));
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setClickable(true);
                view.setBackgroundColor(getResources().getColor(R.color.orange));
            }
        }, 1000 * 60);
        Register.sendVerification(value, new NetRequestCallback() {

            @Override
            public void failure(@NotNull Request request, @NotNull Exception exception) {

            }

            @Override
            public void success(@Nullable String result) {

            }
        });
    }

    public void onQuestionClick() {
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.customer_dialog);
        customerDialog.setCallback(new CustomerDialog.InitCallback() {
            @Override
            public void initWidget(View rootView) {
                rootView.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
                rootView.findViewById(R.id.btn_confirm).setVisibility(View.GONE);
                TextView title = rootView.findViewById(R.id.tv_title);
                title.setText("注册须知");
                TextView content = rootView.findViewById(R.id.tv_content);
                content.setText(getString(R.string.RegisterStandard));
                rootView.findViewById(R.id.btn_got).setOnClickListener(view -> {
                    customerDialog.dismiss();
                });
            }
        });
        customerDialog.show(getSupportFragmentManager(), "a");
    }

    @Override
    public void onBackPressed() {
        // 强制用户通过指示按钮返回
    }


    @Override
    public void failure() {
        Log.d(TAG, "run: ");
        MyToast.showMessage("注册异常");
        stopLoading();
        finish();
    }

    @Override
    public void success() {
        RegisterOptionalUserInfo registerOptionalUserInfo = new RegisterOptionalUserInfo();
        registerOptionalUserInfo.setNickname(mViewModel.getNickName().getValue());
        registerOptionalUserInfo.setAddress(mViewModel.getPhoneNumber().getValue());
        JMessageClient.register(mViewModel.getUserName().getValue().toString(), mViewModel.getPassword().getValue().toString(), registerOptionalUserInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: " + s);
                if (i == 0) {
                    MyToast.showMessage("注册成功");
                } else {
                    MyToast.showMessage(s);
                }
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        stopLoading();
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void success(@Nullable String result) {
        success();
    }

    @Override
    public void failure(@NotNull Request request, @NotNull Exception exception) {
        failure();
    }
}