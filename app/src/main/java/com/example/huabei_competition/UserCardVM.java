package com.example.huabei_competition;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

public class UserCardVM extends AndroidViewModel {
    private MutableLiveData<String> nickName;
    private MutableLiveData<String> userName;
    private MutableLiveData<String> phoneNumber;
    private UserInfo myInfo;

    public UserCardVM(@NonNull Application application) {
        super(application);
        myInfo = JMessageClient.getMyInfo();
        this.nickName = new MutableLiveData<>(myInfo.getNickname());
        this.userName = new MutableLiveData<>(myInfo.getUserName());
        String phoneNumber = myInfo.getAddress();
        if (!TextUtils.isEmpty(phoneNumber)) {
            StringBuffer sb = new StringBuffer(phoneNumber);
            sb.replace(3,9,"*");
            phoneNumber = sb.toString();
        }
        this.phoneNumber = new MutableLiveData<>(phoneNumber);
    }

    public void updateUserInfo() {
        myInfo = JMessageClient.getMyInfo();
        this.nickName = new MutableLiveData<>(myInfo.getNickname());
        this.userName = new MutableLiveData<>(myInfo.getUserName());
        this.phoneNumber = new MutableLiveData<>();
    }

    public UserInfo getMyInfo() {
        return myInfo;
    }

    public MutableLiveData<String> getNickName() {
        return nickName;
    }


    public MutableLiveData<String> getUserName() {
        return userName;
    }


    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }


}
