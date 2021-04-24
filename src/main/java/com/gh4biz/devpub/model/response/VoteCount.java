package com.gh4biz.devpub.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteCount {
    private int id;
    private long total;
}
