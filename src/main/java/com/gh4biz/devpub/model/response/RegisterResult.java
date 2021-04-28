package com.gh4biz.devpub.model.response;

import lombok.Data;


@Data
public class RegisterResult {
    private boolean result;
    private RegError errors;

    public RegisterResult(boolean result, RegError errors) {
        this.result = result;
        this.errors = errors;
    }

    public RegisterResult(boolean result) {
        this.result = result;
    }
}
