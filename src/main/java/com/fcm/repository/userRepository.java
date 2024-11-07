package com.fcm.repository;

import com.fcm.entity.userEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface userRepository extends JpaRepository<userEntity, Long> {
    userEntity findByEmail(String email);
}
