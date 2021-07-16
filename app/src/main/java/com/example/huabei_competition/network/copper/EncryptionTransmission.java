package com.example.huabei_competition.network.copper;

import android.util.Log;

import com.example.huabei_competition.network.copper.XhhEncNew;

public class EncryptionTransmission {


    private static final String TAG = "EncryptionTransmission";

    public static String encode(String body) {
        String enc = XhhEncNew.enc(body);
        Log.d(TAG, "test: \n" + enc);
        return enc;
    }
}
