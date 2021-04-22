package com.gh4biz.devpub.api.response;

import lombok.Data;

import java.util.List;
import java.util.TreeMap;

@Data
public class PostsByYearResponse {
    private List<Integer> years;
    private TreeMap<String, Integer> posts;
}
