package com.example.huabei_competition.fcyUtil;

import android.content.Context;
import android.widget.Toast;
 /**
  *      Create by FanChenYang
  *
  */
public class MyToast  {
    private static Toast toast ;
    public static void showMessage(String message, Context context){
        if(toast == null){
            toast = Toast.makeText(context,message,Toast.LENGTH_LONG);
        }else {
            toast.setText(message);
        }
        toast.show();

    }
}
