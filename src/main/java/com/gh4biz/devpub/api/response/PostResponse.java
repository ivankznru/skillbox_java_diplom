package com.gh4biz.devpub.api.response;

import com.gh4biz.devpub.model.Post;

import java.util.List;

public class PostResponse {
    private int count;
    private List<Post> posts;

    public PostResponse() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
