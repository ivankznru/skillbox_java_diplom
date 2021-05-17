package com.gh4biz.devpub.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Comment {
    @JsonProperty(value = "parent_id")
    private Integer parentId;
    @JsonProperty(value = "post_id")
    private Integer postId;
    private String text;
}
