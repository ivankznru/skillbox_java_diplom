package com.gh4biz.devpub.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileEditForm {
//    private MultipartFile photo;
    private int removePhoto;
    private String name;
    private String email;
    private String password;
}
