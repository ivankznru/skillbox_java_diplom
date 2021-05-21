package com.gh4biz.devpub.model.request;

import lombok.Data;

@Data
public class ProfileEditForm {
    private String name;
    private String email;
    private String password;
}
