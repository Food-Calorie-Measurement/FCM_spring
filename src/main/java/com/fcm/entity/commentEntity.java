package com.fcm.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class commentEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id") // 게시글 ID와 연결
    private boardEntity board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 댓글 작성자 ID와 연결
    private userEntity user;

    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    public commentEntity() {
    }

    public commentEntity(boardEntity board, userEntity user, String content) {
        this.board = board;
        this.user = user;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // 업데이트 메서드 (댓글 내용 변경 시 업데이트 시간을 갱신)
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
