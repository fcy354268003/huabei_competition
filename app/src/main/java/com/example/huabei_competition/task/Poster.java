package com.example.huabei_competition.task;

import android.os.Handler;
import android.os.Looper;

public class Poster extends Handler {

    private Poster() {
        super(Looper.getMainLooper());
    }

    public static Handler getPoster() {
        return PosterImp.sInstance;
    }

    private static class PosterImp {
        private static final Poster sInstance = new Poster();
    }
}
