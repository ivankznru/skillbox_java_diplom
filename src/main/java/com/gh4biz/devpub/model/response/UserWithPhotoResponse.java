package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserWithPhotoResponse {
    private int id;
    private String name;
    private String photo; // "/avatars/ab/cd/ef/52461.jpg"
}
