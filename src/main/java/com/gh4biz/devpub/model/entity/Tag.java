package com.gh4biz.devpub.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id тэга
    private String name; //текст тэга
}
