package com.gh4biz.devpub.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
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

    @Column(columnDefinition = "TEXT")
    private String text; //текст комментария

}
