package com.fcm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcm.entity.calenderEntity;
import com.fcm.entity.imageResultEntity;
import com.fcm.service.calendarService;
import com.fcm.service.imageResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class calendarController {

    @Autowired
    private calendarService service;

    @Autowired
    private imageResultService resultService;

    // 캘린더 자료 업로드
    @PostMapping(value = "/calendar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("entity") String entityJson) throws IOException {

        // JSON 문자열을 calenderEntity 객체로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        calenderEntity entity;
        try {
            entity = objectMapper.readValue(entityJson, calenderEntity.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("JSON 데이터를 파싱할 수 없습니다: " + e.getMessage());
        }

        // 파일 저장 로직
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

    @GetMapping("/calendar/{id}")
    public ResponseEntity<List<?>> getCalendar(@PathVariable Long id) {
        return ResponseEntity.ok(service.findAllCalenderByuserId(id));
    }

    private void saveImageResult(Long id, String imagePath) {
        String flaskUrl = "http://localhost:5000/upload";
        RestTemplate restTemplate = new RestTemplate();

        // 파일 리소스 생성
        File imageFile = new File(imagePath);
        FileSystemResource fileResource = new FileSystemResource(imageFile);

        // multipart/form-data로 파일 전송
        String response = restTemplate.postForObject(flaskUrl, fileResource, String.class);

        // 응답 출력
        System.out.println("Response from Flask: " + response);
    }
}
