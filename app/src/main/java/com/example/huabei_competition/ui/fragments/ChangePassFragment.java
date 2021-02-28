package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huabei_competition.R;
import com.example.huabei_competition.databinding.FragmentChangePassBinding;
import com.example.huabei_competition.network.api.LogIn;
import com.example.huabei_competition.network.api.PasswordRel;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.widget.MyToast;
import com.example.huabei_competition.widget.WidgetUtil;
import com.google.android.material.snackbar.Snackbar;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class ChangePassFragment extends Fragment {


    private FragmentChangePassBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        if (binding != null) {
            ViewGroup parent = (ViewGroup) binding.getRoot().getParent();
            if (parent != null) {
                parent.removeView(binding.getRoot());
                return binding.getRoot();
            }
        }
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_pass, container, false);
        binding.setLifecycleOwner(this);
        WidgetUtil.setCustomerText(binding.tvChage, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        binding.ivBack.setOnClickListener(view -> {
            ((MainActivity) getActivity()).getController().navigateUp();
        });
        binding.btnConfirm.setOnClickListener(con -> {
            String p1 = binding.etOne.getText().toString();
            String p2 = binding.etTwo.getText().toString();
            String old = binding.etOld.getText().toString();
            if (TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2) || TextUtils.isEmpty(old)) {
                MyToast.showMessage("请填写完整");
                return;
            }
            if (!TextUtils.equals(p1, p2)) {
                MyToast.showMessage("两次输入不一致");
                return;
            }
            if (p1.length() >= 7 && p1.length() <= 12) {
                con.setClickable(false);
                PasswordRel.changePass(old, p1, new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Snackbar.make(binding.getRoot(), "网络出错", Snackbar.LENGTH_SHORT).show();
                        con.setClickable(true);
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                String code = jsonObject.getString("code");
                                Log.d(TAG, "onResponse: " + jsonObject.getString("message"));
                                if (code.equals(LogIn.OK)) {
                                    Snackbar.make(binding.getRoot(), "修改成功", Snackbar.LENGTH_SHORT).show();
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ((MainActivity) getActivity()).getController().navigateUp();
                                        }
                                    });
                                } else {
                                    Snackbar.make(binding.getRoot(), "修改失败", Snackbar.LENGTH_SHORT).show();
                                    con.setClickable(true);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        con.setClickable(true);
                    }
                });
            }

        });
        return binding.getRoot();
    }

    private static final String TAG = "ChangePassFragment";
}