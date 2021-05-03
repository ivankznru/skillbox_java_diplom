package com.gh4biz.devpub.model.entity;

import com.gh4biz.devpub.model.entity.Post;
import com.gh4biz.devpub.model.entity.Tag;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tag2post")
public class Tag2Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id связи

    @ManyToOne(cascade = CascadeType.ALL)
    private Post post; //id поста

    @ManyToOne(cascade = CascadeType.ALL)
    private Tag tag; //id тэга

    public Tag2Post() {
    }
}