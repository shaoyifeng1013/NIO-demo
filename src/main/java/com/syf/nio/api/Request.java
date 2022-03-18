package com.syf.nio.api;

import com.google.gson.annotations.SerializedName;

public class Request {
    @SerializedName("message")
    public String msg;

    public Request(String msg) {
        this.msg = msg;
    }
}
