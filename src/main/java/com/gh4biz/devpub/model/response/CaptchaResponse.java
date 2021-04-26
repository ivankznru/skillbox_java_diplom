package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaptchaResponse {
    private String secret;
    private String image;

}
