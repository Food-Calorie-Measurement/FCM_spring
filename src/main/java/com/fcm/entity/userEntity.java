package com.fcm.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fcm.entity.role.userGrade;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class userEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Enumerated(EnumType.STRING)
    private userGrade grade;

    private String username;

    private String email;

    private String password;

    private int age;

    private float height;

    private float weight;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    public userEntity() {}

    public userEntity(userGrade grade, String username, String email, String password, int age, float height, float weight) {
        this.grade = grade;
        this.username = username;
        this.email = email;
        this.password = password;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
}
