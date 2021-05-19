package com.gh4biz.devpub.model;

import lombok.Data;

@Data
public class RegErrors {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
