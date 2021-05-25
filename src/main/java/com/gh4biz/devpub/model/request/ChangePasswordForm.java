package com.gh4biz.devpub.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChangePasswordForm {
    String captcha;
    @JsonProperty("captcha_secret")
    String captchaSecret;
    String code;
    String password;
}
