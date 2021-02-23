package com.example.huabei_competition.widget;

import android.app.Dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;


public class CustomerDialog extends DialogFragment {

    private float dimAmount = 0.5f;

    private int mHeight = 600;
    private int mWidth = 900;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = LayoutInflater.from(requireContext()).inflate(layoutId, container);
        if (callback != null)
            callback.initWidget(inflate);
        return inflate;
    }

    private int layoutId;

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public InitCallback getCallback() {
        return callback;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        if (window == null)
            return;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.height = mHeight;
        attributes.width = mWidth;
        attributes.dimAmount = dimAmount;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setAttributes(attributes);
    }


    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int height) {
        this.mHeight = height;
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int width) {
        this.mWidth = width;
    }

    public float getDimAmount() {
        return dimAmount;
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
    }

    private InitCallback callback;

    public void setCallback(InitCallback callback) {
        this.callback = callback;
    }

    public interface InitCallback {
        void initWidget(View rootView);
    }
}
