package com.gh4biz.devpub.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginResponse {
    private boolean result;

    @JsonProperty("user")
    private UserLoginResponse userLoginResponse;
}
