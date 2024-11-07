package com.fcm.entity.role;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum boardGrade {
    QUESTION, COMMUNITY;

    @JsonCreator
    public static boardGrade fromString(String key) {
        return key == null ? null : boardGrade.valueOf(key.toUpperCase());
    }
}
