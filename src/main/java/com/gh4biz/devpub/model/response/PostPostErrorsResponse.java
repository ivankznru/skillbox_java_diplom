package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PostPostErrorsResponse {
    private boolean result;
    private Map<String, String> errors;

    public PostPostErrorsResponse(boolean result) {
        this.result = result;
    }
}
