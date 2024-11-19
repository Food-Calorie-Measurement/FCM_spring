package com.fcm.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fcm.dto.foodDto;
import com.fcm.entity.calenderEntity;
import com.fcm.service.calendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class calendarController {

    @Autowired
    private calendarService service;

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

        // JSON 응답 파싱
        String responseBody = saveImageResult(path.toString());
        JsonNode rootNode = objectMapper.readTree(responseBody);
        List<String> namesList = objectMapper.convertValue(
                rootNode.get("names"),
                List.class
        );

        // 리스트를 콤마로 연결된 문자열로 변환
        String namesString = String.join(", ", namesList);

        calenderEntity savedEntity = new calenderEntity(
                LocalDateTime.now(), entity.getTitle(), entity.getDescription(), changePathToString, entity.getUserId(), namesString
        );


        service.save(savedEntity);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/calendar/{id}")
    public ResponseEntity<List<foodDto>> getCalendar(@PathVariable Long id) {
        List<calenderEntity> entities = service.findAllCalenderByuserId(id);

        List<foodDto> dtoList = entities.stream().map(entity -> {
            List<String> predictResults = List.of(entity.getPredictResult().split(",\\s*"));

            // 음식별 개수 계산
            Map<String, Long> foodCountMap = predictResults.stream()
                    .collect(Collectors.groupingBy(food -> food, Collectors.counting()));

            // foodNames와 foodKcals 리스트 생성
            List<String> foodNames = new ArrayList<>(foodCountMap.keySet());
            List<Integer> foodKcals = foodNames.stream()
                    .map(food -> calculateKcal(food) * foodCountMap.get(food).intValue())
                    .toList();

            // 총 칼로리 계산
            int totalFoodKcal = foodKcals.stream().mapToInt(Integer::intValue).sum();

            // FoodDto 생성
            return new foodDto(
                    entity.getId(),
                    entity.getDate().toString(),
                    entity.getTitle(),
                    entity.getDescription(),
                    foodNames,
                    foodKcals,
                    totalFoodKcal,
                    entity.getImagePath(),
                    predictResults
            );
        }).collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    private int calculateKcal(String foodName) {
        // 음식명에 따른 칼로리 값을 반환 (예시)
        switch (foodName.toLowerCase()) {
            case "fried_chicken":
                return 500;
            case "kimbap":
                return 320;
            case "ramyeon":
                return 500;
            case "black_noodle":
                return 700;
            case "topokki":
                return 300;
            default:
                return 200; // 기본 칼로리 값
        }
    }

    private String saveImageResult(String imagePath) {
        String flaskUrl = "http://127.0.0.1:6000/upload";
        RestTemplate restTemplate = new RestTemplate();

        // 파일 리소스 생성
        File imageFile = new File(imagePath);
        FileSystemResource fileResource = new FileSystemResource(imageFile);

        // HttpHeaders 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Multipart 요청 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            // POST 요청 전송
            ResponseEntity<String> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // 응답 출력
            System.out.println("Response from Flask: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
