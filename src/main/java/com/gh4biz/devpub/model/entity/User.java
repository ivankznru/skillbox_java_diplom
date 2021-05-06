package com.gh4biz.devpub.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gh4biz.devpub.model.Role;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "users")
public class User {
    public User(Integer id, Integer isModerator) {
        this.id = id;
        this.isModerator = isModerator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //id пользователя

    @Column(name = "is_moderator")
    @JsonProperty("moderation")
    private Integer isModerator; //является ли пользователь модератором (может ли править глобальные настройки сайта и модерировать посты)

    @Column(name = "reg_time")
    private Date regTime; //дата и время регистрации пользователя

    private String name; //имя пользователя
    private String email; //e-mail пользователя
    private String password; //хэш пароля пользователя
    private String code; //код для восстановления пароля

    @Column(columnDefinition = "TEXT")
    private String photo; //фотография (ссылка на файл)

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.regTime = new Date();
        this.isModerator = 0;
    }

    public Role getRole(){
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }

}