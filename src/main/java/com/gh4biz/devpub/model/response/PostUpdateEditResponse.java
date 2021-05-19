package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class PostUpdateEditResponse {
    private boolean result;
    private Map<String, String> errors;

    public PostUpdateEditResponse(boolean result) {
        this.result = result;
    }
}
