package com.fcm.controller;

import com.fcm.dto.boardDto;
import com.fcm.entity.boardEntity;
import com.fcm.entity.role.boardGrade;
import com.fcm.service.boardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class boardController {

    @Autowired
    boardService service;

    // 게시글 작성
    @PostMapping("/post")
    public ResponseEntity<?> createPost(@RequestBody boardEntity post) {
        boardEntity newPost = new boardEntity(post.getGrade(), post.getUserId(), post.getTitle(), post.getContent(), post.getBoardPassword());
        service.save(newPost);

        return ResponseEntity.ok(newPost);
    }

    // 게시판에 맞는 게시글 전체 불러오기
    @GetMapping("/post/{Grade}")
    public List<boardDto> getPosts(@PathVariable String Grade) {
        String gradeStr = Grade.toUpperCase();
        boardGrade grade;
        grade = boardGrade.valueOf(gradeStr);
        List<boardEntity> list = service.findByBoardGrade(grade);
        List<boardDto> dtoList = new ArrayList<>();

        for (boardEntity entity : list) {
            boardDto dto = new boardDto(entity.getBoardId(), entity.getTitle(), entity.getContent(), entity.getUser().getUsername(), entity.getCreateAt(), entity.getUpdateAt());
            dtoList.add(dto);
        }
        return dtoList;
    }

    //게시글 불러오기
    @GetMapping("/post/{Grade}/{id}")
    public ResponseEntity<?> getPost(@PathVariable String Grade, @PathVariable String id) {
        String gradeStr = Grade.toUpperCase();
        boardGrade grade;
        grade = boardGrade.valueOf(gradeStr);
        Long setId = Long.parseLong(id);
        Optional<boardEntity> post = service.getPost(grade, setId);
        boardDto dto = new boardDto(post.get().getBoardId(), post.get().getTitle(), post.get().getContent(), post.get().getUser().getUsername(), post.get().getCreateAt(), post.get().getUpdateAt());
        return ResponseEntity.ok(dto);
    }

    // 게시글 삭제
    @DeleteMapping("/post/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        service.delete(id);

        return ResponseEntity.ok().build();
    }
}
