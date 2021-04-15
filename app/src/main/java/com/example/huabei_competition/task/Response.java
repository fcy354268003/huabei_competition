package com.example.huabei_competition.task;

import java.util.List;
import java.util.Map;

public class Response {
    /**
     * 响应码
     */
    private int responseCode;
    /**
     * 响应数据
     */
    private String result;
    /**
     * 异常
     */
    private Exception exception;

    private Map<String, List<String>> responseHeader;

    public Response(int responseCode,
                    String result,
                    Exception exception,
                    Map<String, List<String>> responseHeader) {
        this.responseCode = responseCode;
        this.result = result;
        this.exception = exception;
        this.responseHeader = responseHeader;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public String getResult() {
        return result;
    }

    public Exception getException() {
        return exception;
    }

}
