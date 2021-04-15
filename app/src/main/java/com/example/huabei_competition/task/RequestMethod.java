package com.example.huabei_competition.task;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    HEAD("HEAD"),
    DELETE("DELETE");
    private String value;

    RequestMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "RequestMethod{" +
                "value='" + value + '\'' +
                '}';
    }
}
