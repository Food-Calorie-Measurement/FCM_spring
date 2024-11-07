package com.fcm.entity;

import com.fcm.entity.role.boardGrade;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class boardEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Enumerated(EnumType.STRING)
    private boardGrade grade;

    // userId를 직접 저장하기 위한 필드
    @Column(name = "user_id", insertable = true, updatable = false)
    private Long userId;

    // 실제로 참조할 User 객체
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private userEntity user;

    private String title;

    private String content;

    private String boardPassword;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<commentEntity> comments = new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updateAt;

    public boardEntity() {}

    public boardEntity(boardGrade grade, Long userId, String title, String content, String boardPassword) {
        this.grade = grade;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.boardPassword = boardPassword;
        this.createAt = LocalDateTime.now();
        this.updateAt = LocalDateTime.now();
    }
}
