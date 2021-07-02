package com.gh4biz.devpub.model.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id комментария

    @ManyToOne
    private PostComment parent; //комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)

    @ManyToOne
//    @JoinColumn(name = "post_id", nullable = false)
    private Post post; //пост, к которому написан комментарий

    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    private User user; //автор комментария

    private Date time; //дата и время комментария

    private String text; //текст комментария

    public PostComment(PostComment parent, Post post, User user, Date time, String text) {
        this.parent = parent;
        this.post = post;
        this.user = user;
        this.time = time;
        this.text = text;
    }
}
