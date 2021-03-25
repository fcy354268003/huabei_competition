package com.example.huabei_competition.widget;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import com.example.huabei_competition.util.MyApplication;


/**
 * Create by FanChenYang
 */
public class MyToast {
    private static Toast toast;
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void showMessage(String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + Thread.currentThread());
                if (toast == null) {
                    toast = Toast.makeText(MyApplication.getApplicationByReflect(), message, Toast.LENGTH_LONG);
                } else {
                    toast.setText(message);
                }
                toast.show();
            }
        });
    }

    private static final String TAG = "MyToast";
}
