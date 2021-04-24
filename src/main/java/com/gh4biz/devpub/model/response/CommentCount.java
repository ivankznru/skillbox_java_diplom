package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentCount {
    private int id;
    private long total;
}
