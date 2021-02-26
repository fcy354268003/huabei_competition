package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.WelcomeCallback;
import com.example.huabei_competition.databinding.FragmentWelcomeBinding;
import com.example.huabei_competition.event.UserUtil;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.WidgetUtil;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

/**
 * Create by FanChenYang at 2021/2/24
 */
public class WelcomeFragment extends Fragment implements WelcomeCallback {


    private FragmentWelcomeBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        binding.setCallback(this);

        return binding.getRoot();
    }

    private static final String TAG = "WelcomeFragment";

    @Override
    public void onShopClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_shopFragment);
    }

    @Override
    public void onDataShowClick() {
        ((MainActivity) getActivity()).getController().navigate(R.id.action_mainFragment_to_dataShowFragment);
    }

    @Override
    public void onSayingClick() {
        CustomerDialog customerDialog = new CustomerDialog();
        customerDialog.setLayoutId(R.layout.fragment_famous_quotes);
        customerDialog.setCancelable(false);
        customerDialog.setmHeight(1000);
        customerDialog.setCallback(view -> {
            view.findViewById(R.id.btn_close).setOnClickListener(v -> {
                customerDialog.dismiss();
            });
            TextView content = view.findViewById(R.id.tv_quote);
            content.setText(((MyApplication) getActivity().getApplication()).loadOneQuote());
            WidgetUtil.setCustomerText(content, WidgetUtil.CUSTOMER_HUAKANGSHAONV);
        });
        customerDialog.show(getActivity().getSupportFragmentManager(), "quote");
    }
}