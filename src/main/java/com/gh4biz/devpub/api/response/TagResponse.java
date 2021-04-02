package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.Tags;

import java.util.List;

public class TagResponse {
    private List<Tags> tagsList;

    public List<Tags> getTagsList() {
        return tagsList;
    }

    public void setTagsList(List<Tags> tagsList) {
        this.tagsList = tagsList;
    }
}
