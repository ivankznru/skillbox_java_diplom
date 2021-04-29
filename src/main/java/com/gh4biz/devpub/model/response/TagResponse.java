package com.gh4biz.devpub.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gh4biz.devpub.model.TagWeight;
import lombok.Data;

import java.util.List;

@Data
public class TagResponse {
    @JsonProperty("tags")
    private List<TagWeight> tagsList;
}
