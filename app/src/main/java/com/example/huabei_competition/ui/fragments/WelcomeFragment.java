package com.example.huabei_competition.ui.fragments;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.huabei_competition.R;
import com.example.huabei_competition.callback.WelcomeCallback;
import com.example.huabei_competition.databinding.FragmentWelcomeBinding;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.MyApplication;
import com.example.huabei_competition.widget.CustomerDialog;
import com.example.huabei_competition.widget.WidgetUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment implements WelcomeCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WelcomeFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WelcomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WelcomeFragment newInstance(String param1, String param2) {
        WelcomeFragment fragment = new WelcomeFragment();
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