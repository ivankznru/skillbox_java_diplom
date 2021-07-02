package com.gh4biz.devpub.model.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id

    @Lob
    @Type(type="org.hibernate.type.ImageType")
    private byte[] image;

    @Column(name = "image_name")
    private String imageName;
}
