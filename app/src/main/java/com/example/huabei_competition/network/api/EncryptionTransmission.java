package com.example.huabei_competition.network.api;

import android.util.Log;

public class EncryptionTransmission {


    /**
     * @param json 待加密的json数据
     * @return json数据加密后的数据
     */

    private static final String TAG = "EncryptionTransmission";

    public static String test(String body) {
        String enc = XhhEncNew.enc(body);
        Log.d(TAG, "test: \n" + enc);
        return XhhEnc.enc(body);
    }
}
