package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.Post4Response;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostsResponse {
    private int count;
    private List<Post4Response> posts;

    public PostsResponse() {
    }
}
