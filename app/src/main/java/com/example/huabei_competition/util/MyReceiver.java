package com.example.huabei_competition.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.huabei_competition.ui.CountDownActivity;
import com.example.huabei_competition.widget.MyToast;

/**
 * Create by FanChenYang
 */
public class MyReceiver extends BroadcastReceiver {
    public static final String ACTION = "fcy_receiver";
    private static final String TAG = "MyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        MyToast.showMessage("学习已暂停");
        Intent intent1 = new Intent(context, CountDownActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }

}
