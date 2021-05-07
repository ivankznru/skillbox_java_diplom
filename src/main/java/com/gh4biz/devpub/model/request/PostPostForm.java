package com.gh4biz.devpub.model.request;

import lombok.Data;

import java.util.List;

@Data
public class PostPostForm {
    private long timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
