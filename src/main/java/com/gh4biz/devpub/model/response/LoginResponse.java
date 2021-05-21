package com.gh4biz.devpub.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginResponse {
    private boolean result;

    @JsonProperty("user")
    private UserLoginResponse userLoginResponse;

    public LoginResponse(boolean result) {
        this.result = result;
    }

    public LoginResponse(boolean result, UserLoginResponse userLoginResponse) {
        this.result = result;
        this.userLoginResponse = userLoginResponse;
    }

    public LoginResponse() {
    }
}
