package com.example.huabei_competition.task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Create by FanChenYang at 2021/4/14
 */
public enum RequestExecutor {
    INSTANCE;
    private ExecutorService mExecutorService;

    RequestExecutor() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * 执行一个请求
     *
     * @param request 请求对象{@link Request}
     */
    public void execute(Request request,HttpCallback listener) {
        mExecutorService.execute(new RequestTask(request,listener));
    }
}
