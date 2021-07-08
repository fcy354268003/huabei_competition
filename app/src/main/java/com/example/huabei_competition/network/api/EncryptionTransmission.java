package com.example.huabei_competition.network.api;

import android.util.Log;

import com.example.huabei_competition.network.cipher.XhhEncNew;

public class EncryptionTransmission {


    private static final String TAG = "EncryptionTransmission";

    public static String test(String body) {
        String enc = XhhEncNew.enc(body);
        Log.d(TAG, "test: \n" + enc);
        return enc;
    }
}
