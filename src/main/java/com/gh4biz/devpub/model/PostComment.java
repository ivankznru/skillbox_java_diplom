package com.gh4biz.devpub.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id комментария

    @ManyToOne(cascade = CascadeType.ALL)
    private Post parent; //комментарий, на который оставлен этот комментарий (может быть NULL, если комментарий оставлен просто к посту)

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post; //пост, к которому написан комментарий

    @ManyToOne(cascade = CascadeType.ALL)
    private User user; //автор комментария

    private Date time; //дата и время комментария

    private String text; //текст комментария

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Post getParent() {
        return parent;
    }

    public void setParent(Post parent) {
        this.parent = parent;
    }

    public Post getPost() {
        return post;
    }

    public void setPostId(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
