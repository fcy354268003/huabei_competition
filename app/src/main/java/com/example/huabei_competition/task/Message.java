package com.example.huabei_competition.task;

public class Message implements Runnable {
    private Response response;
    private HttpCallback callback;

    public Message(Response response, HttpCallback callback) {
        this.response = response;
        this.callback = callback;
    }

    /**
     * 在主线程被回调
     */
    @Override
    public void run() {
        if (response == null)
            return;
        Exception exception = response.getException();
        if (exception != null) {
            callback.onFailure(exception);
        } else {
            callback.onSuccess(response);
        }
    }
}
