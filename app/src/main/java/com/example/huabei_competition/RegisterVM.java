package com.example.huabei_competition;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by FanChenYang at 2021/1/25
 * <p>
 * 注册界面ViewModel
 * </p>
 */
public class RegisterVM extends AndroidViewModel {
    private MutableLiveData<String> password = new MutableLiveData<>("");
    private MutableLiveData<String> userName = new MutableLiveData<>("");
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>("");
    private MutableLiveData<String> verification = new MutableLiveData<>("");
    private MutableLiveData<String> passSec = new MutableLiveData<>("");
    private MutableLiveData<String> nickName = new MutableLiveData<>("");

    public RegisterVM(Application application) {
        super(application);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(MutableLiveData<String> password) {
        this.password = password;
    }

    public MutableLiveData<String> getUserName() {
        return userName;
    }

    public void setUserName(MutableLiveData<String> userName) {
        this.userName = userName;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(MutableLiveData<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public MutableLiveData<String> getVerification() {
        return verification;
    }

    public void setVerification(MutableLiveData<String> verification) {
        this.verification = verification;
    }

    public MutableLiveData<String> getPassSec() {
        return passSec;
    }

    public void setPassSec(MutableLiveData<String> passSec) {
        this.passSec = passSec;
    }

    public MutableLiveData<String> getNickName() {
        return nickName;
    }

    public void setNickName(MutableLiveData<String> nickName) {
        this.nickName = nickName;
    }

    public List<String> getList() {
        List<String> list = new ArrayList<>();
        list.add(nickName.getValue());
        list.add(userName.getValue());
        list.add(password.getValue());
        list.add(passSec.getValue());
        list.add(phoneNumber.getValue());
        list.add(verification.getValue());
        return list;
    }

    /**
     * getUserName().getValue().toString()
     * , mViewModel.getPassword().getValue().toString()
     * , mViewModel.getPhoneNumber().getValue().toString()
     * , mViewModel.getVerification().getValue().toString()
     * <p>
     * "username" : "username",
     * "password" : "password",
     * "phone" : "phone",
     * "verification" : "verification"
     *
     * @return
     */
    public Map<String, String> toParams() {
        Map<String, String> map = new HashMap<>();
        map.put("username", userName.getValue());
        map.put("password", password.getValue());
        map.put("phone", phoneNumber.getValue());
        map.put("verification", verification.getValue());
        return map;
    }


}
