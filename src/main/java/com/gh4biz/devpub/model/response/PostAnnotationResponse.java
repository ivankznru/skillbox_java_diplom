package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostAnnotationResponse {
    int id;
    long timestamp;
    UserResponse user; // id, name
    String title;
    String announce;
    int likeCount;
    int dislikeCount;
    int commentCount;
    int viewCount;
}
