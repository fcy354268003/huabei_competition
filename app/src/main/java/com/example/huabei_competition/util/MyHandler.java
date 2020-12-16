package com.example.huabei_competition.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Create by FanChenYang at 2020/12/13
 * <p>
 * 主要目的：
 * 防止Handler类的内存泄漏
 * 降低代码冗余
 */
public class MyHandler extends Handler {
    private final WeakReference<Context> weakContext;
    // String : context的toString()
    private static HashMap<String, MyHandler> handlerHashMap = null;

    private MyHandler(Context context) {
        weakContext = new WeakReference<>(context);
    }

    /**
     * @param context 上下文
     * @return MyHandler 对象
     */
    public MyHandler obtain(Context context) {
        if (handlerHashMap == null)
            handlerHashMap = new HashMap<>();
        if (!handlerHashMap.containsKey(context.toString())) {
            MyHandler myHandler = new MyHandler(context);
            handlerHashMap.put(context.toString(), myHandler);
        }
        return handlerHashMap.get(context.toString());
    }

    public Context getWeakContext() {
        if (weakContext != null && weakContext.get() != null)
            return weakContext.get();
        return null;
    }


    /**
     * 工具类默认空实现，子类自由继承
     *
     * @param msg Message
     */
    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
    }

    /**
     *
     * @param context 上下文
     * @return 删除成功返回true
     *
     * 在Activity的destroy()中调用，如果在该Activity中使用了obtain()方法
     */

    public static boolean removeByKey(Context context) {

        return handlerHashMap.remove(context.toString()) != null;

    }
}
