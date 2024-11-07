package com.fcm.repository;

import com.fcm.entity.boardEntity;
import com.fcm.entity.role.boardGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface boardRepository extends JpaRepository<boardEntity, Long> {
    List<boardEntity> findBygrade(boardGrade grade);

    Optional<boardEntity> findBygradeAndBoardId(boardGrade grade, Long id);
}
