package com.fcm.controller;

import com.fcm.entity.calenderEntity;
import com.fcm.service.calendarService;
import com.fcm.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
public class calendarController {

    @Autowired
    private calendarService service;

    @Autowired
    private userService userservice;

    // 캘린더 자료 업로드
    @PostMapping(value = "/calendar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestPart("entity") calenderEntity entity) throws IOException {
        byte[] bytes = file.getBytes();
        Path path = Paths.get("src/main/resources/static/" + file.getOriginalFilename());
        Files.write(path, bytes);

        String changePathToString = path.toString();

        calenderEntity savedEntity = new calenderEntity(
                LocalDateTime.now(), entity.getTitle(), entity.getDescription(), changePathToString, entity.getUserId()
        );

        service.save(savedEntity);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/calendar/{id}")
    public ResponseEntity<List<?>> getCalendar(@PathVariable Long id) {
        return ResponseEntity.ok(service.findAllCalenderByuserId(id));
    }
}
