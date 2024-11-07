package com.fcm.repository;

import com.fcm.entity.calenderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface calenderRepository extends JpaRepository<calenderEntity, Long> {
    List<?> findAllByuserId(Long userId);
}
