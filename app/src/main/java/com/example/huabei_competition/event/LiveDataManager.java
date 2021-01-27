package com.example.huabei_competition.event;


import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

/**
 * Create by FanChenYang at 2021/1/27
 * <p>
 * 全局LiveData的控制，使用LiveData控制事件分发
 * </p>
 */
public class LiveDataManager {
    private static volatile LiveDataManager manager;
    private final HashMap<String, MutableLiveData<Object>> storage;

    private LiveDataManager() {
        storage = new HashMap<>();
    }

    public static LiveDataManager getInstance() {
        if (manager == null) {
            synchronized (LiveDataManager.class) {
                if (manager == null) {
                    manager = new LiveDataManager();
                }
            }
        }
        return manager;
    }

    public <T> MutableLiveData<T> with(String key) {
        if (!storage.containsKey(key)) {
            storage.put(key, new MutableLiveData<>());
        }
        return (MutableLiveData<T>) storage.get(key);
    }
}
