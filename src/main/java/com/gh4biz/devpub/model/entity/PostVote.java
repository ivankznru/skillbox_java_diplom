package com.gh4biz.devpub.model.entity;

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

    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
    private User user; //тот, кто поставил лайк / дизлайк

    @ManyToOne
//    @JoinColumn(name = "post_id", nullable = false)
    private Post post; //пост, которому поставлен лайк / дизлайк

    private Date time; //дата и время лайка / дизлайка

    @Column(columnDefinition = "TINYINT")
    private int value; //лайк или дизлайк: 1 или -1

    public PostVote() {
    }
}