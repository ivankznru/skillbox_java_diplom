package com.gh4biz.devpub.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.TreeSet;

@Data
@AllArgsConstructor
public class SearchTree {
    private Post post;
    private TreeSet<String> text;

}
