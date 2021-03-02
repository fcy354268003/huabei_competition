package com.example.huabei_competition.util;


import android.content.Context;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * Create by FanChenYang at 2021/1/28
 * CounterTimer实现倒计时功能
 */
public abstract class MyCountDownTimer {
    private final long mSum;
    private long mInterval;
    private long mPauseTime;
    private boolean isPause;
    private boolean isCancelled;
    private MyHandler mHandler;
    public static final int MSG = 1;
    private long mStopTimeFuture;

    /**
     * @param sum      毫秒数
     * @param interval 毫秒数
     */
    public MyCountDownTimer(Context context, long sum, long interval) {
        Log.d(TAG, "MyCountDownTimer: ");
        mHandler = MyHandler.obtain(context, message -> {
            Log.d(TAG, "MyCountDownTimer: ");
            if (isPause || isCancelled)
                return;
            long millisLeft = mStopTimeFuture - SystemClock.elapsedRealtime();
            if (millisLeft <= 0) {
                onFinish();
            } else if (millisLeft < mInterval) {
                mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG), millisLeft);
            } else {
                long lastTickStart = SystemClock.elapsedRealtime();
                onTick(millisLeft);
                long delay = lastTickStart + mInterval - SystemClock.elapsedRealtime();
                while (delay < 0) delay += mInterval;
                mHandler.sendMessageDelayed(Message.obtain(mHandler, MSG), delay);
            }
        });
        mSum = sum;
        mInterval = interval;
        Log.d(TAG, "MyCountDownTimer: " + mSum + "\n" + mInterval);
        isCancelled = false;
        isPause = false;
    }

    private static final String TAG = "MyCountDownTimer";

    public void cancel() {
        isCancelled = true;
        mHandler.removeMessages(MSG);
    }

    public void pause() {
        cancel();
        mPauseTime = SystemClock.elapsedRealtime();
        isPause = true;
    }

    public void restart() {
        isPause = false;
        isCancelled = false;
        mStopTimeFuture = mStopTimeFuture + (SystemClock.elapsedRealtime() - mPauseTime);
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public MyCountDownTimer start() {
        if (mSum <= 0) {
            onFinish();
            return this;
        }
        Log.d(TAG, "start: ");
        mStopTimeFuture = SystemClock.elapsedRealtime() + mSum;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }
    public boolean isPause(){
        return isPause;
    }
    /**
     * @param millisLeft 剩余的毫秒数
     */
    public abstract void onTick(long millisLeft);

    public abstract void onFinish();

}
