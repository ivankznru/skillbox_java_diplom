package com.gh4biz.devpub.model;

import lombok.Data;

@Data
public class RegError {
    String email;
    String name;
    String password;
    String captcha;
}
