package com.gh4biz.devpub.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "captcha_codes")
public class CaptchaCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id каптча

    private Date time; //дата и время генерации кода капчи

    private String code; //код, отображаемый на картинкке капчи

    @Column(name = "secret_code")
    private String secretCode; //код, передаваемый в параметре

}
