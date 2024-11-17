package com.fcm.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class calenderEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;

    private String title;

    private String description;

    // userId를 직접 저장하기 위한 필드
    @Column(name = "user_id", insertable = true, updatable = false)
    private Long userId;

    // 실제로 참조할 User 객체
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private userEntity user;

    private String imagePath;

    @Column(length = 500)
    private String predictResult;

    public calenderEntity() {}

    public calenderEntity(LocalDateTime date, String title, String description, String imagePath, Long userId, String predictResult) {
        this.date = date;
        this.title = title;
        this.description = description;
        this.imagePath = imagePath;
        this.userId = userId;
        this.predictResult = predictResult;
    }
}
