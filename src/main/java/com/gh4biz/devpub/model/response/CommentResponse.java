package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponse {
    private int id;
    private long timestamp;
    private String text;
    private UserWithPhotoResponse user;
}