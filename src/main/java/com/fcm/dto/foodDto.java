package com.fcm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class foodDto {
    private Long id;
    private String date;
    private String title;
    private String description;
    private List<String> foodNames; // 수정된 필드
    private List<Integer> foodKcals; // 수정된 필드
    private int totalFoodKcal;
    private String imagePath;
    private List<String> predictResults; // 수정된 필드

    public foodDto() {}

    // 생성자
    public foodDto(Long id, String date, String title, String description,
                   List<String> foodNames, List<Integer> foodKcals, int totalFoodKcal,
                   String imagePath, List<String> predictResults) {
        this.id = id;
        this.date = date;
        this.title = title;
        this.description = description;
        this.foodNames = foodNames;
        this.foodKcals = foodKcals;
        this.totalFoodKcal = totalFoodKcal;
        this.imagePath = imagePath;
        this.predictResults = predictResults;
    }
}