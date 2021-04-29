package com.gh4biz.devpub.model.entity;

import com.gh4biz.devpub.model.ModerationStatus;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id поста

    @Column(name = "is_active", columnDefinition = "TINYINT")
    private int isActive; //скрыта или активна публикация: 0 или 1

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "enum")
    private ModerationStatus status; //статус модерации, по умолчанию значение "NEW"

    @ManyToOne(cascade = CascadeType.ALL)
    private User moderator; //ID пользователя-модератора, принявшего решение

    @ManyToOne(cascade = CascadeType.ALL)
    private User user; //автор поста

    private Date time; //дата и время публикации поста
    private String title; //заголовок поста

    @Column(columnDefinition = "TEXT")
    private String text; //текст поста

    @Column(name = "view_count")
    private int viewCount; //количество просмотров поста

}