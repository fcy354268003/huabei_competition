package com.example.huabei_competition.ui.activity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import androidx.lifecycle.ViewModelProvider;

import com.example.huabei_competition.R;
import com.example.huabei_competition.RegisterVM;
import com.example.huabei_competition.databinding.ActivityRegist1Binding;
import com.example.huabei_competition.util.BaseActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.util.MyHandler;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.widget.MyToast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import cn.jpush.im.api.BasicCallback;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Create by FanChenYang
 */
public class RegisterActivity extends BaseActivity implements Callback {
    private ActivityRegist1Binding mBinding;
    private static final String URL = MyApplication.URL + "/app/regist";
    private final MyHandler myHandler = MyHandler.obtain(this,null);
    private RegisterVM mViewModel;
    // 极光注册成功 + 1，自己的后端注册成功 +1，该值为2 则代表注册成功 否则为注册失败
    private int isOk = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isOk = 1;
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
        if (!UserUtil.register(mViewModel.getList(), this, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Log.d(TAG, "gotResult: " + s);
                if (i == 0) {
                    isOk++;
                }
                check();
            }
        })) {
            MyToast.showMessage("注册出错");
            return;
        }
        mBinding.userName.setVisibility(View.GONE);
        mBinding.password.setVisibility(View.GONE);
        startLoading();
        mBinding.btnCancel.setVisibility(View.GONE);
        mBinding.btnConfirm.setVisibility(View.GONE);
    }

    //检查现在是否可以成功
    private void check() {
        if (isOk == 2) {
            MyToast.showMessage("注册成功");
        }else {
            MyToast.showMessage("注册失败");
        }
        stopLoading();
        finish();
    }

    @Override
    public void onBackPressed() {
        // 强制用户通过指示按钮返回
    }

    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        myHandler.post(new Runnable() {
            @Override
            public void run() {
                MyToast.showMessage("注册失败");
                stopLoading();
                finish();
            }
        });
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        if (response.isSuccessful())
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    isOk++;
                    check();
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