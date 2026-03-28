package com.yunbian.adsscoring.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EchoRequest {

    @NotBlank(message = "message must not be blank")
    private String message;

    @NotNull(message = "sid must not be null")
    private Long sid;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSid() {
        return sid;
    }

    public void setSid(Long sid) {
        this.sid = sid;
    }
}