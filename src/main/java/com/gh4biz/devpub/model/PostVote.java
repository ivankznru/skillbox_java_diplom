package com.gh4biz.devpub.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "post_votes")
public class PostVote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id лайка/дизлайка

    @ManyToOne(cascade = CascadeType.ALL)
    private User user; //тот, кто поставил лайк / дизлайк

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post; //пост, которому поставлен лайк / дизлайк

    private Date time; //дата и время лайка / дизлайка

    private int value; //лайк или дизлайк: 1 или -1

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUserId(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}