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

public abstract class BaseFragment extends Fragment {
    private ViewDataBinding binding;

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
    protected void setListener() {
    }

    public <T extends ViewDataBinding> T getBinding(@NotNull Class<T> clazz) {
        if (binding.getClass().getCanonicalName().equals(clazz.getCanonicalName()))
            return (T) binding;
        else {
            Exception exception = new Exception();
            exception.printStackTrace();
        }
        return (T) binding;

    }

    protected abstract int setLayoutID();
}
