package com.example.huabei_competition.widget;

import android.graphics.Typeface;
import android.widget.TextView;

import com.example.huabei_competition.util.MyApplication;

import java.util.HashMap;

public class WidgetUtil {

    private static HashMap<String, Typeface> typefaceHashMap = new HashMap<>();
    public static final String CUSTOMER_HUAKANGSHAONV = "fonts/DFPShaoNvW5-GB.ttf";

    public static void setCustomerText(TextView textView, String customerStyle) {
        if (!typefaceHashMap.containsKey(customerStyle)) {
            Typeface typeface = Typeface.createFromAsset(MyApplication.getApplicationByReflect().getAssets(), customerStyle);
            typefaceHashMap.put(customerStyle, typeface);
        }
        textView.setTypeface(typefaceHashMap.get(customerStyle));
    }
}
