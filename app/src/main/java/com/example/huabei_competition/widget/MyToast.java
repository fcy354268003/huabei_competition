package com.example.huabei_competition.widget;

import android.content.Context;
import android.widget.Toast;

import com.example.huabei_competition.util.MyApplication;

/**
  *      Create by FanChenYang
  *
  */
public class MyToast  {
    private static Toast toast ;
    public static void showMessage(String message){
        if(toast == null){
            toast = Toast.makeText(MyApplication.getApplicationByReflect(),message,Toast.LENGTH_LONG);
        }else {
            toast.setText(message);
        }
        toast.show();

    }
}
