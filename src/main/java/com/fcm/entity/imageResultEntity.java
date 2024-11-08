package com.fcm.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class imageResultEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    private String imageName;

    private String imageResult;

    public imageResultEntity() {}

    public imageResultEntity(String imageUrl, String imageName, String imageResult) {
        this.imageUrl = imageUrl;
        this.imageName = imageName;
        this.imageResult = imageResult;
    }
}
