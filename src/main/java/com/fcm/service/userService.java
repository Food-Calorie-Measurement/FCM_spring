package com.fcm.service;

import com.fcm.entity.userEntity;
import com.fcm.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class userService {
    @Autowired
    userRepository repository;

    public void join(userEntity user) {
        repository.save(user);
    }

    public userEntity login(String email) {
        return repository.findByEmail(email);
    }

    public boolean findJoinUser(String email) {
        return repository.findByEmail(email)==null;
    }

    public String findUseremail(String email) {
        return repository.findByEmail(email).getEmail();
    }
}
