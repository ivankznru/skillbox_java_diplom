package com.gh4biz.devpub.model.response;

import lombok.Data;

import java.util.Map;

@Data
public class ProfileEditResponse {
    private boolean result;
    private Map<String, String> errors;

    public ProfileEditResponse(boolean result) {
        this.result = result;
    }
}
