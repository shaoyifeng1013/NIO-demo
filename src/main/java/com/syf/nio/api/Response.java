package com.syf.nio.api;

import com.google.gson.annotations.SerializedName;

public class Response {
    int code;
    @SerializedName("message")
    String msg;

    public Response(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
