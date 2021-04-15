package com.example.huabei_competition.task;

import com.example.huabei_competition.util.Logger;

/**
 * Create by FanChenYang at 2021/4/15
 */
public class RequestTask implements Runnable {
    private Request request;
    private HttpCallback callback;
    public RequestTask(Request request,HttpCallback callback) {
        this.request = request;
        this.callback = callback;
    }

    @Override
    public void run() {
        //执行请求
        Logger.i(request.toString());
        Response response = null;
        Message message = new Message(response,callback);
        Poster.getPoster().post(message);
    }
}
