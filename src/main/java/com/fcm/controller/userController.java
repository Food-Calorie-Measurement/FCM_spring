package com.fcm.controller;

import com.fcm.BCryptService;
import com.fcm.dto.loginRequestDTO;
import com.fcm.entity.userEntity;
import com.fcm.entity.role.userGrade;
import com.fcm.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class userController {

    @Autowired
    userService service;

    @PostMapping(value = "/join")
    public ResponseEntity<?> join(@RequestBody userEntity user) {
        BCryptService bcryptService = new BCryptService();
        String hashPassword = bcryptService.encodeBcrypt(user.getPassword(), 10);

        userEntity userEntity = new userEntity(userGrade.USER, user.getUsername(), user.getEmail(), hashPassword, user.getAge(), user.getHeight(), user.getWeight());

        if (service.findJoinUser(user.getEmail())) {
            service.join(userEntity);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        return ResponseEntity.ok(userEntity);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody loginRequestDTO request) {
        userEntity entity = service.login(request.getEmail());
        BCryptService bcryptService = new BCryptService();

        if (entity != null && bcryptService.matchesBcrypt(request.getPassword(), entity.getPassword(), 10)) {
            return ResponseEntity.ok(entity);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
    }
}
