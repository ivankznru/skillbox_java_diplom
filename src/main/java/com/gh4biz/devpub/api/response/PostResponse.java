package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.Post;
import com.gh4biz.devpub.model.Post4Response;
import lombok.Data;

import java.util.List;

@Data
public class PostResponse {
    private int count;
    private int offset;
    private String mode;
    private List<Post4Response> posts;
}
