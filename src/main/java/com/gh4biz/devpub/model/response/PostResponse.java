package com.gh4biz.devpub.model.response;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PostResponse {
    private int id;
    private long timestamp;
    private boolean active;
    private UserResponse user; // id, name
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private ArrayList<CommentResponse> comments;
//    private List<String> tags;
//    private Post post;
}
