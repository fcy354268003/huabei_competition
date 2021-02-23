package com.example.huabei_competition;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.huabei_competition.util.MyCountDownTimer;

public class TimerVM extends ViewModel {
    private MutableLiveData<Long> minute;
    private MutableLiveData<Long> second;

    public TimerVM(long time) {
        minute = new MutableLiveData<>(time);
        second = new MutableLiveData<>(0L);
    }

    public MutableLiveData<Long> getMinute() {
        return minute;
    }

    public MutableLiveData<Long> getSecond() {
        return second;
    }

    public void setMinute(MutableLiveData<Long> minute) {
        this.minute = minute;
    }

    public void setSecond(MutableLiveData<Long> second) {
        this.second = second;
    }
}
