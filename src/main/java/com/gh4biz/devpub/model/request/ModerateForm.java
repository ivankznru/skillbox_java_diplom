package com.gh4biz.devpub.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ModerateForm {
    @JsonProperty("post_id")
    int postId;
    String decision;
}
