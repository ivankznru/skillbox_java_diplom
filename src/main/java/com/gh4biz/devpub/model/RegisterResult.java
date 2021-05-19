package com.gh4biz.devpub.model;

import lombok.Data;


@Data
public class RegisterResult {
    private boolean result;
    private RegErrors errors;

    public RegisterResult(boolean result, RegErrors errors) {
        this.result = result;
        this.errors = errors;
    }

    public RegisterResult(boolean result) {
        this.result = result;
    }
}
