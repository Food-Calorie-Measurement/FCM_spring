package com.fcm.dto;

import com.fcm.entity.role.userGrade;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class userDto {
    private Long userId;
    private userGrade grade;
    private String username;
    private String email;
    private String password;
    private int age;
    private float height;
    private float weight;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

    public userDto() {}

    public userDto(Long userId, String username, String email) {
        this.userId = userId;
        this.username = username;
        this.email = email;
    }
}
