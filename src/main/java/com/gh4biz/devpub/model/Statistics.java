package com.gh4biz.devpub.model;

import lombok.Data;

@Data
public class Statistics {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
