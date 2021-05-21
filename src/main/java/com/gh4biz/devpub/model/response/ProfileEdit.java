package com.gh4biz.devpub.model.response;

import lombok.Data;

import java.util.Map;

@Data
public class ProfileEdit {
    private boolean result;
    private Map<String, String> errors;

    public ProfileEdit(boolean result) {
        this.result = result;
    }

    public ProfileEdit(boolean result, Map<String, String> errors) {
        this.result = result;
        this.errors = errors;
    }
}
