package com.gh4biz.devpub.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id тэга
    private String name; //текст тэга

    public Tag(String tagName) {
        this.name = tagName;
    }

    public Tag() {
    }

    @OneToMany(targetEntity=Tag2Post.class, mappedBy="tag",cascade=CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tag2Post> tag2PostList;
}
