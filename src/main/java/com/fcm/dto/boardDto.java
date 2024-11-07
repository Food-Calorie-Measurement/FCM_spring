package com.fcm.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class boardDto {
    private Long id;
    private String title;
    private String content;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boardDto() {}

    public boardDto(Long id, String title, String content, String userName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userName = userName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
