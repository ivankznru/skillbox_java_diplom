package com.gh4biz.devpub.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Post4Response{
    int id;
    long timestamp;
    User4Response user; // id, name
    String title;
    String announce;
    int likeCount;
    int dislikeCount;
    int commentCount;
    int viewCount;
    //long sort;

    public Post4Response() {
    }

//    @Override
//    public int compareTo(Post4Response post4Response) {
//        if (getSort() > post4Response.getSort()){
//            return 1;
//        }
//        if (getSort() < post4Response.getSort()){
//            return -1;
//        }
//        return 0;
//    }
}
