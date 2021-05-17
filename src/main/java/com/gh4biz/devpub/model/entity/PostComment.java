package com.gh4biz.devpub.model.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_comments")
@RequiredArgsConstructor
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id комментария

    @ManyToOne(cascade = CascadeType.ALL)
    private PostComment parent; //комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post; //пост, к которому написан комментарий

    @ManyToOne(cascade = CascadeType.ALL)
    private User user; //автор комментария

    private Date time; //дата и время комментария

    @Column(columnDefinition = "TEXT")
    private String text; //текст комментария

    public PostComment(PostComment parent, Post post, User user, Date time, String text) {

        this.parent = parent;
        this.post = post;
        this.user = user;
        this.time = time;
        this.text = text;
    }
}
