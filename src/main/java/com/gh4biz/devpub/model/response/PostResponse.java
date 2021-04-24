package com.gh4biz.devpub.model.response;

import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.service.PostService;
import lombok.Data;

import java.util.List;

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
    private List<CommentResponse> comments;
    private List<String> tags;
    private Post post;

    public PostResponse(Post post) {
        this.post = post;
        this.id = post.getId();
        this.timestamp = post.getTime().getTime() / 1000;
        if (post.getIsActive() == 1) {
            this.active = true;
        }
        this.user = new UserResponse(
                post.getUser().getId(),
                post.getUser().getName());
        this.title = post.getTitle();
        this.text = post.getText();
        this.likeCount = PostService.getVoteCount(post, 1);
        this.dislikeCount = PostService.getVoteCount(post, -1);
        this.viewCount = post.getViewCount();
    }
}
