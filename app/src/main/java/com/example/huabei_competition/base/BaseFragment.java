package com.example.huabei_competition.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {
    protected T binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding != null) {
            ViewGroup viewGroup = (ViewGroup) binding.getRoot().getParent();
            if (viewGroup != null) {
                viewGroup.removeView(binding.getRoot());
            }
            return binding.getRoot();
        }
        binding = DataBindingUtil.inflate(inflater, setLayoutID(), container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        setListener();
        return binding.getRoot();
    }

    /**
     * 设置点击事件
     */
    protected abstract void setListener();

    public T getBinding() {
        return binding;

    }

    protected abstract int setLayoutID();
}
