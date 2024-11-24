package com.fcm.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.*;
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

        String savedImagePath = path.toString();

        // Flask 서버의 응답 처리
        String responseBody = saveImageResult(savedImagePath, file.getOriginalFilename());
        JsonNode rootNode;
        System.out.println(responseBody);
        try {
            rootNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Flask 서버 응답이 유효한 JSON 형식이 아닙니다.");
        }

        // Flask 응답 데이터 검증
        String base64Image = rootNode.path("imagePath").asText(null);
        JsonNode namesNode = rootNode.path("names");
        if (base64Image == null || !namesNode.isArray()) {
            return ResponseEntity.badRequest().body("Flask 서버 응답 데이터가 유효하지 않습니다.");
        }

        // 음식 이름 리스트 추출
        List<String> namesList = new ArrayList<>();
        for (JsonNode nameNode : namesNode) {
            namesList.add(nameNode.asText());
        }
        String namesString = String.join(", ", namesList);

        // 엔티티 저장
        calenderEntity savedEntity = new calenderEntity(
                LocalDateTime.now(),
                entity.getTitle(),
                entity.getDescription(),
                base64Image,
                entity.getUserId(),
                namesString
        );
        service.save(savedEntity);

        return ResponseEntity.ok().body("이미지 업로드 및 처리 완료");
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

    private String saveImageResult(String imagePath, String originalFilename) {
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
            ResponseEntity<Map> response = restTemplate.exchange(
                    flaskUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            // 응답 처리
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // base64 이미지 디코딩 및 저장
                String base64Image = (String) responseBody.get("image");
                List<String> names = (List<String>) responseBody.get("names"); // names 리스트 가져오기

                if (base64Image != null) {
                    byte[] decodedBytes = Base64.getDecoder().decode(base64Image);

                    // src/main/resources/static/images 폴더 생성
                    String outputFolderPath = "src/main/resources/static/images";
                    File outputFolder = new File(outputFolderPath);
                    if (!outputFolder.exists()) {
                        boolean created = outputFolder.mkdirs();
                        if (!created) {
                            return "{\"error\": \"Failed to create output folder.\"}";
                        }
                    }

                    // 파일 저장 경로 설정
                    String outputFileName = originalFilename; // 저장할 파일 이름
                    Path outputFilePath = Paths.get(outputFolderPath, outputFileName);
                    Files.write(outputFilePath, decodedBytes);

                    System.out.println("Image saved to: " + outputFilePath.toAbsolutePath());

                    // JSON 응답 생성
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> result = new HashMap<>();
                    result.put("imagePath", outputFilePath.toAbsolutePath().toString());
                    result.put("names", names);

                    return objectMapper.writeValueAsString(result); // JSON 문자열 반환
                } else {
                    return "{\"error\": \"Image not found in response.\"}";
                }
            } else {
                return "{\"error\": \"Failed to get a valid response from Flask.\"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
