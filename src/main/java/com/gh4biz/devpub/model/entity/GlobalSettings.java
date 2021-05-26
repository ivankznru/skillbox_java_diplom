package com.gh4biz.devpub.model.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "global_settings")
public class GlobalSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //id настройки
    private String code; //системное имя настройки
    private String name; //название настройки
    private String value; //значение настройки

    public GlobalSettings() {
    }
}
