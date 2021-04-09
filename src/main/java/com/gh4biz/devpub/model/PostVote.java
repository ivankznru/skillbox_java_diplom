package com.gh4biz.devpub.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
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

    @Column(columnDefinition = "TINYINT")
    private int value; //лайк или дизлайк: 1 или -1

}