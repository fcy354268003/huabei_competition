package com.example.huabei_competition.ui.fragments;


import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.huabei_competition.R;
import com.example.huabei_competition.base.BaseFragment;
import com.example.huabei_competition.databinding.FragmentInfoPerfectionBinding;
import com.example.huabei_competition.db.City;
import com.example.huabei_competition.ui.activity.MainActivity;
import com.example.huabei_competition.util.GsonUtil;
import com.example.huabei_competition.util.Logger;
import com.example.huabei_competition.widget.ArrayWheelAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Create by FanChenYang at 2021/4/15
 * 用户信息完善界面
 */
public class InfoPerfectionFragment extends BaseFragment<FragmentInfoPerfectionBinding> {

    @Override
    protected int setLayoutID() {
        return R.layout.fragment_info_perfection;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentInfoPerfectionBinding binding = getBinding();
        binding.wvRegion.setTextSize(20);
        binding.wvRegion.setAdapter(initRegionAdapter());
        initLabels();
    }

    private ArrayWheelAdapter<String> initRegionAdapter() {
        ArrayWheelAdapter<String> arrayWheelAdapter = null;
        arrayWheelAdapter = new ArrayWheelAdapter<>(getListFromRaw());
        return arrayWheelAdapter;
    }

    private List<String> getListFromRaw() {
        StringBuilder sb = new StringBuilder();
        String sos = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.regison_json)))) {
            while ((sos = br.readLine()) != null) {
                sb.append(sos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        sos = sb.toString();
        Logger.d(sos);
        City city = GsonUtil.fromJson(sos, City.class);
        List<String> res = new ArrayList<>();
        for (City.Cn cn : city.getList()) {
            res.add(cn.getName());
        }
        return res;
    }

    private void initLabels() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 15;
        layoutParams.bottomMargin = 10;
        FragmentInfoPerfectionBinding binding = getBinding();
        for (int i = 0; i < 20; i++) {
            binding.fvLabels.addView(createLabel("haha" + i + i), layoutParams);
        }
    }

    private CheckBox createLabel(String name) {
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setBackground(getResources().getDrawable(R.drawable.checkitem));
        checkBox.setAlpha(0.6f);
        checkBox.setTextSize(20);
        checkBox.setPadding(20, 0, 20, 0);
        checkBox.setButtonDrawable(null);
        checkBox.setText(name);
        checkBox.setTextColor(getResources().getColor(R.color.createTextColor));
        return checkBox;
    }

    @Override
    protected void setListener() {
        getBinding().ivNext.setOnClickListener(view -> {
            ((MainActivity) getActivity()).getController().navigateUp();
        });
    }


}