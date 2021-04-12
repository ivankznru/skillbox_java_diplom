package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.Post;
import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private int count;
    private List<Post> posts;
}
