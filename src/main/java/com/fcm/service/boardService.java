package com.fcm.service;

import com.fcm.entity.boardEntity;
import com.fcm.entity.role.boardGrade;
import com.fcm.repository.boardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class boardService {
    @Autowired
    boardRepository repository;

    public void save(boardEntity newPost) {
        repository.save(newPost);
    }

    public List<boardEntity> findByBoardGrade(boardGrade grade) {
        return repository.findBygrade(grade);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Optional<boardEntity> getPost(boardGrade grade, Long id) {
        return repository.findBygradeAndBoardId(grade, id);
    }
}
