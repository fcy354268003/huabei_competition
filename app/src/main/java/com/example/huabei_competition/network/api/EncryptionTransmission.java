package com.example.huabei_competition.network.api;

public class EncryptionTransmission {
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * @param json 待加密的json数据
     * @return json数据加密后的数据
     */
    public native String getEncryption(String json);
}
