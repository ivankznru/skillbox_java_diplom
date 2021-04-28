package com.gh4biz.devpub.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterForm {
    private String name;
    @JsonProperty("e_mail")
    private String email;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String secret;
}
