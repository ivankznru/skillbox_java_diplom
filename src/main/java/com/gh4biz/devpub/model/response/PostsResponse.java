package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsResponse {
    private int count;
    private List<PostAnnotationResponse> posts;

    public PostsResponse() {
    }
}
