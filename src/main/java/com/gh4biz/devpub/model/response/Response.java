package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response {

    private boolean result;
    private String message;

    public Response(String message) {
        this.message = message;
    }

    public Response(boolean result) {
        this.result = result;
    }
}