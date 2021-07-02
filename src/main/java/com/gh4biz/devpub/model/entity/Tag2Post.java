package com.gh4biz.devpub.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tag2post")
public class Tag2Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id связи

    @ManyToOne(cascade = CascadeType.PERSIST)
 //   @JoinColumn(name="post_id")
    public Post post; //id поста

    @ManyToOne(cascade = CascadeType.PERSIST)
   // @JoinColumn(name="tag_id")
    private Tag tag; //id тэга

    public Tag2Post() {
    }

    public Tag2Post(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
    }
}