package com.gh4biz.devpub.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCount {
    private int id;
    private long total;
}
